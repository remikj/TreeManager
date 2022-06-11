package rj.treemanager.tree.node.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import rj.treemanager.tree.node.TreeNode;
import rj.treemanager.tree.node.exceptions.TreeNodeNotFoundException;
import rj.treemanager.tree.node.repository.TreeNodeRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreeNodeChildSavingServiceTest {

    private static final Long EXAMPLE_PARENT_ID = 1L;
    @Mock
    private TreeNodeRepository treeNodeRepository;
    @Spy
    private TreeNodeSumToRootService treeNodeSumToRootService;
    @Captor
    private ArgumentCaptor<TreeNode> treeNodeArgumentCaptor;
    @InjectMocks
    private TreeNodeChildSavingService treeNodeChildSavingService;

    @Test
    void saveChild_shouldSaveChild_whenParentNodeExists() {
        var parentNode = createTreeNode(EXAMPLE_PARENT_ID, 6);
        when(treeNodeRepository.getNode(EXAMPLE_PARENT_ID)).thenReturn(parentNode);

        treeNodeChildSavingService.saveChild(EXAMPLE_PARENT_ID, new TreeNode(5));

        verify(treeNodeRepository).save(treeNodeArgumentCaptor.capture());

        TreeNode actualTreeNode = treeNodeArgumentCaptor.getValue();
        assertEquals(parentNode.getId(), actualTreeNode.getParentNode().getId());
        assertEquals(11, actualTreeNode.getSumToRoot());
    }

    @Test
    void saveChildWithChildrenForParentId_shouldSaveChildWithChildren_whenParentNodeExists() {
        var parentNode = createTreeNode(EXAMPLE_PARENT_ID, 6);
        when(treeNodeRepository.getNode(EXAMPLE_PARENT_ID)).thenReturn(parentNode);

        var childNode1 = new TreeNode(1);
        var childNode2 = new TreeNode(2);
        var node = new TreeNode(EXAMPLE_PARENT_ID + 1, 3, 0, null, List.of(childNode1, childNode2));
        when(treeNodeRepository.save(node)).thenReturn(node);
        when(treeNodeRepository.save(childNode1)).thenReturn(childNode1);
        when(treeNodeRepository.save(childNode2)).thenReturn(childNode2);

        treeNodeChildSavingService.saveChildWithChildrenForParentId(EXAMPLE_PARENT_ID, node);

        verify(treeNodeRepository, times(3)).save(treeNodeArgumentCaptor.capture());
        var savedList = treeNodeArgumentCaptor.getAllValues();
        var firstSaved = savedList.get(0);
        var secondSaved = savedList.get(1);
        var thirdSaved = savedList.get(2);

        assertEquals(EXAMPLE_PARENT_ID, firstSaved.getParentNode().getId());
        assertEquals(9, firstSaved.getSumToRoot());
        assertEquals(EXAMPLE_PARENT_ID+1, secondSaved.getParentNode().getId());
        assertEquals(10, secondSaved.getSumToRoot());
        assertEquals(EXAMPLE_PARENT_ID+1, thirdSaved.getParentNode().getId());
        assertEquals(11, thirdSaved.getSumToRoot());
    }

    @Test
    void saveChild_shouldRethrowTreeNodeRepositoryException() {
        when(treeNodeRepository.getNode(EXAMPLE_PARENT_ID)).thenThrow(new TreeNodeNotFoundException("not found"));

        assertThrows(TreeNodeNotFoundException.class, () -> treeNodeChildSavingService.saveChild(EXAMPLE_PARENT_ID, new TreeNode(5)));
    }

    @Test
    void copyTo_shouldSucceed() {
    }

    private TreeNode createTreeNode(Long nodeId, int nodeSumToRoot) {
        return new TreeNode(nodeId, 1, nodeSumToRoot, null, null);
    }
}