package rj.treemanager.tree.node.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rj.treemanager.tree.node.TreeNode;
import rj.treemanager.tree.node.exceptions.TreeNodeNotFoundException;

import java.util.List;

@Repository
public interface TreeNodeRepository extends CrudRepository<TreeNode, Long> {

    List<TreeNode> findAll();

    List<TreeNode> findByParentNode(TreeNode parentNode);

    default TreeNode getNode(Long nodeId) {
        return this.findById(nodeId)
                .orElseThrow(() -> new TreeNodeNotFoundException("Node with id: " + nodeId + " not found"));
    }
}
