package rj.treemanager.tree.node.service;

import org.springframework.stereotype.Service;
import rj.treemanager.tree.node.TreeNode;

/**
 * Service for updating TreeNode sumToRoot
 */
@Service
public class TreeNodeSumToRootService {

    /**
     * Update treeNode sumToRoot using its value and parentSumToRoot
     *
     * @param treeNode        node which sumToRoot will be updated
     * @param parentSumToRoot value of parentSumToRoot
     */
    public void updateSumToRoot(TreeNode treeNode, long parentSumToRoot) {
        treeNode.setSumToRoot(treeNode.getValue() + parentSumToRoot);
    }

    /**
     * Update treeNode sumToRoot using its value and parentNode
     * If parentNode is null use zero as parent sumToRoot
     * If parentNode is not nul use its sumToRoot
     *
     * @param treeNode   node which sumToRoot will be updated
     * @param parentNode parent node
     */
    public void updateSumToRoot(TreeNode treeNode, TreeNode parentNode) {
        if (parentNode != null) {
            updateSumToRoot(treeNode, parentNode.getSumToRoot());
        } else {
            updateSumToRoot(treeNode, 0);
        }
    }

}
