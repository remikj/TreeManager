package rj.treemanager.tree.node.root;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rj.treemanager.tree.node.TreeNode;
import rj.treemanager.tree.node.exceptions.TreeNodeNotFoundException;
import rj.treemanager.tree.node.repository.TreeNodeRepository;
import rj.treemanager.tree.node.service.TreeNodeChildSavingService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RootNodeBeanTest {

    @Mock
    private TreeNodeRepository treeNodeRepository;
    @Mock
    private TreeNodeChildSavingService treeNodeChildService;
    @Captor
    ArgumentCaptor<TreeNode> treeNodeArgumentCaptor;
    @InjectMocks
    private RootNodeBean rootNodeBean;

    @Test
    void afterPropertiesSet_shouldDoNothing_whenOnePossibleRootNodeInRepository() {
        when(treeNodeRepository.findByParentNode(null)).thenReturn(List.of(new TreeNode()));

        rootNodeBean.afterPropertiesSet();

        verifyNoMoreInteractions(treeNodeRepository, treeNodeChildService);
    }

    @Test
    void afterPropertiesSet_shouldCreateRootNode_whenOnePossibleRootNodeInRepository() {
        when(treeNodeRepository.findByParentNode(null)).thenReturn(List.of());

        rootNodeBean.afterPropertiesSet();

        verify(treeNodeRepository).save(treeNodeArgumentCaptor.capture());
        TreeNode savedNode = treeNodeArgumentCaptor.getValue();
        assertNull(savedNode.getParentNode());
        assertEquals(0, savedNode.getSumToRoot());
        assertEquals(0, savedNode.getValue());
        assertNull(savedNode.getChildren());
        assertNull(savedNode.getId());
    }

    @Test
    void afterPropertiesSet_shouldThrowException_whenMoreThanOnePossibleRootNodeInRepository() {
        when(treeNodeRepository.findByParentNode(null)).thenReturn(List.of(new TreeNode(), new TreeNode()));

        assertThrows(RootNodeException.class, () -> rootNodeBean.afterPropertiesSet());

        verifyNoMoreInteractions(treeNodeRepository, treeNodeChildService);
    }

    @Test
    void getRootNodeId_shouldReturnRootNodeId() {
        when(treeNodeRepository.findByParentNode(null))
                .thenReturn(List.of(new TreeNode(1L, 0, 0, null, null)));

        Long actualRootNodeId = rootNodeBean.getRootNodeId();

        assertEquals(1L, actualRootNodeId);
    }

    @Test
    void getRootNode_shouldReturnRootNode_whenOneRootNodeInRepository() {
        TreeNode expectedRootNode = new TreeNode(1L, 0, 0, null, null);
        when(treeNodeRepository.findByParentNode(null))
                .thenReturn(List.of(expectedRootNode));

        TreeNode actualRootNode = rootNodeBean.getRootNode();

        assertEquals(expectedRootNode, actualRootNode);
    }

    @Test
    void getRootNode_shouldThrowException_whenNoRootNodeInRepository() {
        when(treeNodeRepository.findByParentNode(null))
                .thenReturn(List.of());

        assertThrows(TreeNodeNotFoundException.class, () -> rootNodeBean.getRootNode());
    }

    @Test
    void getRootNode_shouldThrowException_whenTooManyRootNodesInRepository() {
        when(treeNodeRepository.findByParentNode(null))
                .thenReturn(List.of(new TreeNode(), new TreeNode()));

        assertThrows(RootNodeException.class, () -> rootNodeBean.getRootNode());
    }

    @Test
    void resetRootNode_shouldDeleteChildrenAndSetNodeValueToZero() {
        List<TreeNode> children = List.of(new TreeNode(), new TreeNode());
        TreeNode expectedRootNode = new TreeNode(1L, 5, 5, null, children);
        when(treeNodeRepository.findByParentNode(null)).thenReturn(List.of(expectedRootNode));
        
        rootNodeBean.resetRootNode();

        verify(treeNodeRepository).delete(expectedRootNode);
        verify(treeNodeChildService).saveChildWithChildrenForParent(eq(null), treeNodeArgumentCaptor.capture());
        TreeNode value = treeNodeArgumentCaptor.getValue();
        assertNull(value.getChildren());
        assertEquals(0, value.getValue());
        assertEquals(0, value.getSumToRoot());
    }

    @Test
    void overrideRootNode() {
    }
}