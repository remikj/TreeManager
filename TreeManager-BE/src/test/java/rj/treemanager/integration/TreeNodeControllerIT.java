package rj.treemanager.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import rj.treemanager.tree.node.TreeNode;
import rj.treemanager.tree.node.repository.TreeNodeRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rj.treemanager.TestUtil.assertEqualTree;
import static rj.treemanager.TestUtil.createNodeWithValueSumToRootAndChildren;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles
@AutoConfigureMockMvc
@Sql({"/insertDataToDb.sql"})
@Sql(value = {"/cleanupDb.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TreeNodeControllerIT {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TreeNodeRepository treeNodeRepository;

    @Test
    void getRootNode() throws Exception {
        var expected = createDefaultTree();

        getNodeAndExpect(1, expected);
    }

    @Test
    void getNodeWithId2() throws Exception {
        var expected = createDefaultTree().getChildren().get(0);

        getNodeAndExpect(2, expected);
    }

    @Test
    void getNotExistingNode_shouldReturnNotFoundStatus() throws Exception {
        mockMvc.perform(get("/tree/nodes/-200"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteNodeWithId2() throws Exception {
        var expected = createNodeWithValueSumToRootAndChildren(6, 6, List.of(
                createNodeWithValueSumToRootAndChildren(3, 9, null)
        ));

        mockMvc.perform(delete("/tree/nodes/2"))
                .andExpect(status().isOk());

        assertEquals(2, treeNodeRepository.findAll().size());
        getNodeAndExpect(1, expected);
    }

    @Test
    void deleteNotExistingNode_shouldReturnNotFoundStatus() throws Exception {
        mockMvc.perform(delete("/tree/nodes/-200"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addChild_shouldAddChildToNode() throws Exception {
        var nodeToAdd = new TreeNode(-2);
        var expected = createNodeWithValueSumToRootAndChildren(5, 11, List.of(
                createNodeWithValueSumToRootAndChildren(17, 28, null),
                createNodeWithValueSumToRootAndChildren(10, 21, List.of(
                        createNodeWithValueSumToRootAndChildren(-5, 16, null)
                )),
                createNodeWithValueSumToRootAndChildren(-2, 9, null)
        ));

        mockMvc.perform(post("/tree/nodes/2/addChild")
                .content(objectMapper.writeValueAsBytes(nodeToAdd))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        assertEquals(7, treeNodeRepository.findAll().size());
        getNodeAndExpect(2, expected);
    }

    @Test
    void addChildWithChildren_shouldAddChildWithChildrenToNode() throws Exception {
        var nodeToAdd = new TreeNode(-2, List.of(new TreeNode(-17)));
        var expected = createNodeWithValueSumToRootAndChildren(5, 11, List.of(
                createNodeWithValueSumToRootAndChildren(17, 28, null),
                createNodeWithValueSumToRootAndChildren(10, 21, List.of(
                        createNodeWithValueSumToRootAndChildren(-5, 16, null)
                )),
                createNodeWithValueSumToRootAndChildren(-2, 9, List.of(
                        createNodeWithValueSumToRootAndChildren(-17, -8, null)
                ))
        ));

        mockMvc.perform(post("/tree/nodes/2/addChildWithChildren")
                .content(objectMapper.writeValueAsBytes(nodeToAdd))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        assertEquals(8, treeNodeRepository.findAll().size());
        getNodeAndExpect(2, expected);
    }

    @Test
    void copyTo_shouldCopyChildWithChildrenToNode() throws Exception {
        var expected = createNodeWithValueSumToRootAndChildren(6, 6, List.of(
                createNodeWithValueSumToRootAndChildren(5, 11, List.of(
                        createNodeWithValueSumToRootAndChildren(17, 28, null),
                        createNodeWithValueSumToRootAndChildren(10, 21, List.of(
                                createNodeWithValueSumToRootAndChildren(-5, 16, null)
                        ))
                )),
                createNodeWithValueSumToRootAndChildren(3, 9, List.of(
                        createNodeWithValueSumToRootAndChildren(5, 14, List.of(
                                createNodeWithValueSumToRootAndChildren(17, 31, null),
                                createNodeWithValueSumToRootAndChildren(10, 24, List.of(
                                        createNodeWithValueSumToRootAndChildren(-5, 19, null)
                                ))
                        ))
                ))
        ));

        mockMvc.perform(post("/tree/nodes/2/copyTo/3"))
                .andExpect(status().isOk());

        assertEquals(10, treeNodeRepository.findAll().size());
        getNodeAndExpect(1, expected);
    }

    @Test
    void moveTo_shouldMoveChildWithChildrenToNode() throws Exception {
        var expected = createNodeWithValueSumToRootAndChildren(6, 6, List.of(
                createNodeWithValueSumToRootAndChildren(3, 9, List.of(
                        createNodeWithValueSumToRootAndChildren(5, 14, List.of(
                                createNodeWithValueSumToRootAndChildren(17, 31, null),
                                createNodeWithValueSumToRootAndChildren(10, 24, List.of(
                                        createNodeWithValueSumToRootAndChildren(-5, 19, null)
                                ))
                        ))
                ))
        ));

        mockMvc.perform(post("/tree/nodes/2/moveTo/3"))
                .andExpect(status().isOk());

        assertEquals(6, treeNodeRepository.findAll().size());
        getNodeAndExpect(1, expected);
    }

    private void getNodeAndExpect(long id, TreeNode expected) throws Exception {
        var mvcResult = mockMvc.perform(get("/tree/nodes/" + id))
                .andExpect(status().isOk())
                .andReturn();
        var actual = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), TreeNode.class);

        assertNull(actual.getParentNode());
        assertEqualTree(expected, actual);
    }

    private TreeNode createDefaultTree() {
        return createNodeWithValueSumToRootAndChildren(6, 6, List.of(
                createNodeWithValueSumToRootAndChildren(5, 11, List.of(
                        createNodeWithValueSumToRootAndChildren(17, 28, null),
                        createNodeWithValueSumToRootAndChildren(10, 21, List.of(
                                createNodeWithValueSumToRootAndChildren(-5, 16, null)
                        ))
                )),
                createNodeWithValueSumToRootAndChildren(3, 9, null)
        ));
    }
}
