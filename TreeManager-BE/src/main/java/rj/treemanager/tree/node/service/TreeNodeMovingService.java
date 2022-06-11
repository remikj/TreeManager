package rj.treemanager.tree.node.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rj.treemanager.tree.node.TreeNode;
import rj.treemanager.tree.node.exceptions.BadActionException;
import rj.treemanager.tree.node.repository.TreeNodeRepository;

/**
 * Service for copying and moving nodes in tree.
 */
@Service
@AllArgsConstructor
public class TreeNodeMovingService {

    private final TreeNodeRepository treeNodeRepository;
    private final TreeNodeChildSavingService treeNodeChildSavingService;


    /**
     * Copy node with nodeId as child of node with targetParentNodeId.
     * Node is copied with children and sumToRoot values are updated.
     *
     * @param nodeId             id of node to copy
     * @param targetParentNodeId id of target parent node
     */
    public void copyTo(Long nodeId, Long targetParentNodeId) {
        var nodeToCopy = treeNodeRepository.getNode(nodeId);
        var newNode = new TreeNode(nodeToCopy);
        treeNodeChildSavingService.saveChildWithChildrenForParentId(targetParentNodeId, newNode);
    }

    /**
     * Move node with nodeId as child of node with targetParentNodeId.
     * Node is moved with children and sumToRoot values are updated.
     * Node cannot be moved to its child.
     *
     * @param nodeId             id of node to move
     * @param targetParentNodeId id of target parent node
     */
    public void moveTo(Long nodeId, Long targetParentNodeId) {
        var nodeToMove = treeNodeRepository.getNode(nodeId);
        verifyTargetNodeIdNotInMovedNode(nodeToMove, targetParentNodeId);
        var newNode = new TreeNode(nodeToMove);
        treeNodeChildSavingService.saveChildWithChildrenForParentId(targetParentNodeId, newNode);
        treeNodeRepository.deleteById(nodeId);
    }

    private void verifyTargetNodeIdNotInMovedNode(TreeNode nodeToMove, Long targetParentNodeId) {
        if (nodeToMove.getId().equals(targetParentNodeId)) {
            throw BadActionException.cannotMoveNodeToItsChild();
        }
        if (nodeToMove.getChildNodes() == null) return;
        for (var childNode : nodeToMove.getChildNodes()) {
            verifyTargetNodeIdNotInMovedNode(childNode, targetParentNodeId);
        }
    }
}
