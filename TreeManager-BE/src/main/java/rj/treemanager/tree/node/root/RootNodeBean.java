package rj.treemanager.tree.node.root;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rj.treemanager.tree.node.TreeNode;
import rj.treemanager.tree.node.exceptions.TreeNodeNotFoundException;
import rj.treemanager.tree.node.repository.TreeNodeRepository;
import rj.treemanager.tree.node.service.TreeNodeChildSavingService;

/**
 * Service for root node management and initialization
 */
@Service
@Transactional
public class RootNodeBean implements InitializingBean, RootNode {

    private final TreeNodeRepository treeNodeRepository;
    private final TreeNodeChildSavingService treeNodeChildSavingService;

    public RootNodeBean(TreeNodeRepository treeNodeRepository, TreeNodeChildSavingService treeNodeChildService) {
        this.treeNodeRepository = treeNodeRepository;
        this.treeNodeChildSavingService = treeNodeChildService;
    }

    /**
     * Check if repository has a root node.
     * If not, then create a new zero value root node.
     * If there are more than one possible root nodes, throw exception.
     * Do nothing if there is exactly one root node.
     */
    @Override
    public void afterPropertiesSet() {
        var nodesWithoutParent = treeNodeRepository.findByParentNode(null);
        if (nodesWithoutParent.size() > 1) {
            throw RootNodeException.tooManyRootNodes();
        } else if (nodesWithoutParent.isEmpty()) {
            createRootNode();
        }
    }

    @Override
    public Long getRootNodeId() {
        return getRootNode().getId();
    }

    /**
     * Get root node checking if repository has a root node.
     * If not, throw exception.
     * If there are more than one possible root nodes, throw exception.
     */
    @Override
    public TreeNode getRootNode() {
        var nodesWithoutParent = treeNodeRepository.findByParentNode(null);
        if (nodesWithoutParent.size() > 1) {
            throw RootNodeException.tooManyRootNodes();
        } else if (nodesWithoutParent.isEmpty()) {
            throw new TreeNodeNotFoundException("Root node not found");
        }
        return nodesWithoutParent.get(0);
    }

    /**
     * Reset tree to single zero value root node
     */
    @Override
    public void resetRootNode() {
        overrideRootNode(new TreeNode(0));
    }

    /**
     * Discard tree and save new root node with its children as new tree.
     *
     * @param newRootNode new root node
     */
    @Override
    public void overrideRootNode(TreeNode newRootNode) {
        TreeNode rootNode = getRootNode();
        treeNodeRepository.delete(rootNode);
        treeNodeChildSavingService.saveChildWithChildrenForParent(null, newRootNode);
    }

    private void createRootNode() {
        verifyTreeRepositoryIsEmpty();
        treeNodeRepository.save(new TreeNode(0));
    }

    private void verifyTreeRepositoryIsEmpty() {
        if (!treeNodeRepository.findAll().isEmpty()) {
            throw new RootNodeException("Tree repository has no root node, but has elements");
        }
    }
}
