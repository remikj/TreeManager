package rj.treemanager.tree.node.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rj.treemanager.tree.node.TreeNode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TreeNodeUpdateServiceTest {

    @Mock
    TreeNodeService treeNodeService;
    @Mock
    TreeNodeChildSavingService treeNodeChildSavingService;
    @Captor
    ArgumentCaptor<TreeNode> argumentCaptor;
    @InjectMocks
    TreeNodeUpdateService treeNodeUpdateService;

    @Test
    void update_shouldUpdateValueAndSaveUpdatedNodeWithChildren_whenNewValueDifferent() {
        var nodeToUpdateId = 3L;
        var childrenList = List.of(new TreeNode(5));
        var nodeToUpdate = new TreeNode(nodeToUpdateId, 3, 6, null, childrenList);
        var newValue = 15;
        when(treeNodeService.getNode(nodeToUpdateId)).thenReturn(nodeToUpdate);

        treeNodeUpdateService.update(nodeToUpdateId, new TreeNode(newValue));

        verify(treeNodeChildSavingService).saveChildWithChildrenForParent(any(), argumentCaptor.capture());
        assertEquals(newValue, argumentCaptor.getValue().getValue());
        assertEquals(childrenList, argumentCaptor.getValue().getChildNodes());
    }

    @Test
    void update_shouldNotUpdate_whenNewValueNotDifferent() {
        var nodeToUpdateId = 3L;
        var childrenList = List.of(new TreeNode(5));
        var value = 15;
        var nodeToUpdate = new TreeNode(nodeToUpdateId, value, 6, null, childrenList);
        when(treeNodeService.getNode(nodeToUpdateId)).thenReturn(nodeToUpdate);

        treeNodeUpdateService.update(nodeToUpdateId, new TreeNode(value));

        verifyNoInteractions(treeNodeChildSavingService);
    }

    @Test
    void update_shouldRethrowTreeNodeServiceException() {
        var nodeToUpdateId = 3L;
        RuntimeException treeNodeServiceException = new RuntimeException("treeNodeService exception");
        when(treeNodeService.getNode(nodeToUpdateId)).thenThrow(treeNodeServiceException);

        assertThrowsExactly(
                RuntimeException.class,
                () -> treeNodeUpdateService.update(nodeToUpdateId, new TreeNode(3)),
                treeNodeServiceException.getMessage()
        );
    }

    @Test
    void update_shouldRethrowTreeNodeChildServiceException() {
        var nodeToUpdateId = 3L;
        var nodeToUpdate = new TreeNode(nodeToUpdateId, 3, 6, null, null);
        when(treeNodeService.getNode(nodeToUpdateId)).thenReturn(nodeToUpdate);
        RuntimeException treeNodeChildServiceException = new RuntimeException("treeNodeChildService exception");
        doThrow(treeNodeChildServiceException).when(treeNodeChildSavingService).saveChildWithChildrenForParent(any(), any());

        assertThrowsExactly(
                RuntimeException.class,
                () -> treeNodeUpdateService.update(nodeToUpdateId, new TreeNode(5)),
                treeNodeChildServiceException.getMessage()
        );
    }
}