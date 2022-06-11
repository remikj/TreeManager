package rj.treemanager.tree.node.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rj.treemanager.tree.node.TreeNode;

/**
 * Service for updating TreeNode value.
 */
@Service
@AllArgsConstructor
@Transactional
public class TreeNodeUpdateService {

    private final TreeNodeService treeNodeService;
    private TreeNodeChildSavingService treeNodeChildSavingService;

    /**
     * Update node with given nodeId with value from given node, and save it with children.
     *
     * @param nodeId id of node to update value
     * @param node   node object holding new value
     */
    public void update(Long nodeId, TreeNode node) {
        TreeNode nodeToUpdate = treeNodeService.getNode(nodeId);
        if (nodeToUpdate.getValue() != node.getValue()) {
            nodeToUpdate.setValue(node.getValue());
            treeNodeChildSavingService.saveChildWithChildrenForParent(nodeToUpdate.getParentNode(), nodeToUpdate);
        }
    }
}
