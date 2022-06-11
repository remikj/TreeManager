package rj.treemanager.tree.node.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rj.treemanager.tree.node.TreeNode;
import rj.treemanager.tree.node.exceptions.BadActionException;
import rj.treemanager.tree.node.repository.TreeNodeRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreeNodeMovingServiceTest {

    @Mock
    private TreeNodeRepository treeNodeRepository;
    @Mock
    private TreeNodeChildSavingService treeNodeChildSavingService;
    @InjectMocks
    TreeNodeMovingService treeNodeMovingService;
    @Captor
    ArgumentCaptor<TreeNode> treeNodeArgumentCaptor;

    private static final Long SOURCE_ID = 1L;
    private static final Long TARGET_PARENT_NODE_ID = 2L;

    @Test
    void copyTo_shouldCopyNodeWithChildren() {
        TreeNode sourceNode = new TreeNode(SOURCE_ID, 1, 1, null, List.of(
                new TreeNode(3L, 2, 3, null, null)));
        when(treeNodeRepository.getNode(SOURCE_ID)).thenReturn(sourceNode);

        treeNodeMovingService.copyTo(SOURCE_ID, TARGET_PARENT_NODE_ID);

        verify(treeNodeChildSavingService).saveChildWithChildrenForParentId(eq(TARGET_PARENT_NODE_ID), treeNodeArgumentCaptor.capture());
        TreeNode actualSavedNode = treeNodeArgumentCaptor.getValue();
        assertTreeNodesCopied(sourceNode, actualSavedNode);
    }

    @Test
    void moveTo_shouldMoveNodeWithChildren() {
        TreeNode sourceNode = new TreeNode(SOURCE_ID, 1, 1, null, List.of(
                new TreeNode(3L, 2, 3, null, null)));
        when(treeNodeRepository.getNode(SOURCE_ID)).thenReturn(sourceNode);

        treeNodeMovingService.moveTo(SOURCE_ID, TARGET_PARENT_NODE_ID);

        verify(treeNodeChildSavingService).saveChildWithChildrenForParentId(eq(TARGET_PARENT_NODE_ID), treeNodeArgumentCaptor.capture());
        verify(treeNodeRepository).deleteById(SOURCE_ID);
        TreeNode actualSavedNode = treeNodeArgumentCaptor.getValue();
        assertTreeNodesCopied(sourceNode, actualSavedNode);
    }

    @Test
    void moveTo_shouldFail_whenTargetParentNodePartOfNodeBeingMoved() {
        TreeNode sourceNode = new TreeNode(SOURCE_ID, 1, 1, null, List.of(
                new TreeNode(3L, 2, 3, null, null)));
        when(treeNodeRepository.getNode(SOURCE_ID)).thenReturn(sourceNode);

        assertThrows(BadActionException.class, () -> treeNodeMovingService.moveTo(SOURCE_ID, 3L));

        verifyNoMoreInteractions(treeNodeRepository);
        verifyNoInteractions(treeNodeChildSavingService);
    }

    private void assertTreeNodesCopied(TreeNode sourceNode, TreeNode actualSavedNode) {
        assertNotEquals(sourceNode, actualSavedNode);
        assertEquals(sourceNode.getValue(), actualSavedNode.getValue());
        assertNull(actualSavedNode.getId());
        if (sourceNode.getChildNodes() != null) {
            assertEquals(sourceNode.getChildNodes().size(), actualSavedNode.getChildNodes().size());
            for (var i = 0; i < sourceNode.getChildNodes().size(); i++) {
                assertTreeNodesCopied(sourceNode.getChildNodes().get(i), actualSavedNode.getChildNodes().get(i));
            }
        }
    }
}