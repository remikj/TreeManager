package rj.treemanager.tree.node.service;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rj.treemanager.tree.node.TreeNode;
import rj.treemanager.tree.node.exceptions.BadActionException;
import rj.treemanager.tree.node.repository.TreeNodeRepository;
import rj.treemanager.tree.node.root.RootNode;

/**
 * Service for simple TreeNode operations: get, delete
 */
@Service
@AllArgsConstructor
@Transactional
public class TreeNodeService {

    private final TreeNodeRepository treeNodeRepository;
    private final RootNode rootNode;

    @NotNull
    public TreeNode getNode(Long nodeId) {
        return treeNodeRepository.getNode(nodeId);
    }

    /**
     * Delete node with given id, not allowing deletion of root node
     *
     * @param nodeId id of node to delete
     */
    public void deleteNode(Long nodeId) {
        if (rootNode.getRootNodeId().equals(nodeId)) {
            throw BadActionException.cannotDeleteRootNode();
        }
        treeNodeRepository.deleteById(nodeId);
    }

}
