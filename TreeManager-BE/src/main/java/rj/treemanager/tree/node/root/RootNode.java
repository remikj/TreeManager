package rj.treemanager.tree.node.root;

import rj.treemanager.tree.node.TreeNode;

public interface RootNode {
    Long getRootNodeId();
    TreeNode getRootNode();
    void resetRootNode();
    void overrideRootNode(TreeNode newRootNode);
}
