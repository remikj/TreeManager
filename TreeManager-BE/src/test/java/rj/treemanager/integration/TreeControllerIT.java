package rj.treemanager.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import rj.treemanager.TestUtil;
import rj.treemanager.tree.node.TreeNode;
import rj.treemanager.tree.node.repository.TreeNodeRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rj.treemanager.TestUtil.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
class TreeControllerIT {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TreeNodeRepository treeNodeRepository;

    @Test
    @Order(1)
    void getTree() throws Exception {
        testGetTreeForDefaultTree();
    }

    @Test
    @Order(2)
    void overrideTree() throws Exception {
        var rootNode = new TreeNode(1, List.of(
                new TreeNode(2, List.of(
                        new TreeNode(3),
                        new TreeNode(10)
                ))
        ));
        mockMvc.perform(put("/tree")
                .content(objectMapper.writeValueAsBytes(rootNode))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        var allNodes = treeNodeRepository.findAll();
        assertEquals(4, allNodes.size());
    }

    @Test
    @Order(3)
    void getTreeAfterOverride() throws Exception {
        var expected = createNodeWithValueSumToRootAndChildNodes(1, 1, List.of(
                createNodeWithValueSumToRootAndChildNodes(2, 3, List.of(
                        createNodeWithValueSumToRootAndChildNodes(3, 6, null),
                        createNodeWithValueSumToRootAndChildNodes(10, 13, null)
                ))
        ));
        var mvcResult = mockMvc.perform(get("/tree"))
                .andExpect(status().isOk())
                .andReturn();
        var actual = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), TreeNode.class);

        assertEqualTree(expected, actual);
    }

    @Test
    @Order(4)
    void resetTree() throws Exception {
        mockMvc.perform(delete("/tree"))
                .andExpect(status().isOk())
                .andReturn();

        List<TreeNode> allNodes = treeNodeRepository.findAll();
        assertEquals(1, allNodes.size());
    }

    @Test
    @Order(5)
    void getTreeAfterReset() throws Exception {
        testGetTreeForDefaultTree();
    }


    private void testGetTreeForDefaultTree() throws Exception {
        var expected = TestUtil.createDefaultRootNode();

        var mvcResult = mockMvc.perform(get("/tree"))
                .andExpect(status().isOk())
                .andReturn();
        var actual = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), TreeNode.class);

        assertNull(actual.getParentNode());
        assertEqualTree(expected, actual);
        assertEquals(1, treeNodeRepository.findAll().size());
    }
}