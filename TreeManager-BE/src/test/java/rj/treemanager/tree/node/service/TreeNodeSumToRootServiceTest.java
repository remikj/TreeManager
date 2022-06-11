package rj.treemanager.tree.node.service;

import org.junit.jupiter.api.Test;
import rj.treemanager.tree.node.TreeNode;

import static org.junit.jupiter.api.Assertions.*;

class TreeNodeSumToRootServiceTest {

    TreeNodeSumToRootService treeNodeSumToRootService = new TreeNodeSumToRootService();

    @Test
    void updateSumToRoot_shouldAddValueToParentLongAndSetSumToRoot() {
        var node = new TreeNode(3);

        treeNodeSumToRootService.updateSumToRoot(node, 5);

        assertEquals(8, node.getSumToRoot());
    }


    @Test
    void updateSumToRoot_shouldAddValueToParentSumToRootAndSetSumToRoot() {
        var node = new TreeNode(3);
        var parentNode = new TreeNode(1L, 0, 7, null, null);

        treeNodeSumToRootService.updateSumToRoot(node, parentNode);

        assertEquals(10, node.getSumToRoot());
    }

    @Test
    void updateSumToRoot_shouldSetValueAsSumToRoot_whenParentNodeIsNull() {
        var node = new TreeNode(3);

        treeNodeSumToRootService.updateSumToRoot(node, null);

        assertEquals(3, node.getSumToRoot());
    }

}