package rj.treemanager.tree.node.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rj.treemanager.tree.node.TreeNode;
import rj.treemanager.tree.node.repository.TreeNodeRepository;

/**
 * Service for saving TreeNode child-parent relationships
 */
@Service
@AllArgsConstructor
@Transactional
public class TreeNodeChildSavingService {
    private final TreeNodeRepository treeNodeRepository;
    private final TreeNodeSumToRootService treeNodeSumToRootService;

    /**
     * Save child node with calculated sumToRoot and reference to a parent.
     * Child nodes of treeNode are ignored.
     *
     * @param parentId id of target parent node
     * @param treeNode node that should be added as child to node with parentId
     */
    public void saveChild(Long parentId, TreeNode treeNode) {
        TreeNode parentNode = treeNodeRepository.getNode(parentId);
        saveChild(parentNode, treeNode);
    }

    /**
     * Save child node and its children with calculated sumToRoot and reference to a parent.
     *
     * @param parentId id of target parent node
     * @param treeNode node that should be added as child to node with parentId
     */
    public void saveChildWithChildrenForParentId(Long parentId, TreeNode treeNode) {
        TreeNode parentNode = treeNodeRepository.getNode(parentId);
        saveChildWithChildrenForParent(parentNode, treeNode);
    }

    /**
     * Save child node and its children with calculated sumToRoot and reference to a parent.
     *
     * @param parentNode target parent node
     * @param treeNode   node that should be added as child to parentNode
     */
    public void saveChildWithChildrenForParent(TreeNode parentNode, TreeNode treeNode) {
        var childNodes = treeNode.getChildNodes();
        var savedNode = saveChild(parentNode, treeNode);
        if (childNodes == null) {
            return;
        }
        for (var child : childNodes) {
            saveChildWithChildrenForParent(savedNode, child);
        }
    }

    private TreeNode saveChild(TreeNode parentNode, TreeNode treeNode) {
        treeNode.setParentNode(parentNode);
        treeNodeSumToRootService.updateSumToRoot(treeNode, parentNode);
        return treeNodeRepository.save(treeNode);
    }
}
