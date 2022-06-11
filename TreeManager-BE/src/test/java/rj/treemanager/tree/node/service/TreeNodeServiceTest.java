package rj.treemanager.tree.node.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import rj.treemanager.tree.node.TreeNode;
import rj.treemanager.tree.node.exceptions.BadActionException;
import rj.treemanager.tree.node.exceptions.TreeNodeNotFoundException;
import rj.treemanager.tree.node.repository.TreeNodeRepository;
import rj.treemanager.tree.node.root.RootNode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TreeNodeServiceTest {

    private static final Long EXAMPLE_ID = 1L;

    @Mock
    private RootNode rootNode;
    @Mock
    private TreeNodeRepository repository;
    @InjectMocks
    private TreeNodeService treeNodeService;

    @Test
    void getNode_shouldReturnNodeWithChildren() {
        TreeNode expectedNode = createDefaultNodeWithChildren();
        when(repository.getNode(EXAMPLE_ID)).thenReturn(expectedNode);

        TreeNode node = treeNodeService.getNode(EXAMPLE_ID);

        assertEquals(expectedNode, node);
    }

    @Test
    void getNode_shouldRethrowRepositoryException_whenTreeNodeNotInRepository() {
        when(repository.getNode(EXAMPLE_ID)).thenThrow(new TreeNodeNotFoundException("not found"));

        assertThrowsExactly(TreeNodeNotFoundException.class, () -> treeNodeService.getNode(EXAMPLE_ID));
    }

    @Test
    void deleteNode_shouldDeleteNodeFromRepository_whenNodeIsNotRoot() {
        when(rootNode.getRootNodeId()).thenReturn(EXAMPLE_ID + 1);

        treeNodeService.deleteNode(EXAMPLE_ID);
    }

    @Test
    void deleteNode_shouldThrowException_whenNodeIsRoot() {
        when(rootNode.getRootNodeId()).thenReturn(EXAMPLE_ID);

        assertThrows(BadActionException.class, () -> treeNodeService.deleteNode(EXAMPLE_ID));
    }

    @Test
    void deleteNode_shouldRethrowRepositoryException() {
        when(rootNode.getRootNodeId()).thenReturn(EXAMPLE_ID + 1);
        var expectedException = new EmptyResultDataAccessException(1);
        doThrow(expectedException).when(repository).deleteById(EXAMPLE_ID);

        assertThrowsExactly(EmptyResultDataAccessException.class, () -> treeNodeService.deleteNode(EXAMPLE_ID), expectedException.getMessage());
    }

    private TreeNode createDefaultNodeWithChildren() {
        return new TreeNode(EXAMPLE_ID, 0, 0, null, List.of(
                new TreeNode(EXAMPLE_ID + 1, 0, 0, null, null),
                new TreeNode(EXAMPLE_ID + 2, 0, 0, null, null)
        ));
    }

    private TreeNode createDefaultNodeWithoutChildren() {
        return new TreeNode(EXAMPLE_ID, 0, 0, null, null);
    }
}