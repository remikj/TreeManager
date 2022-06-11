package rj.treemanager.tree.node;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import rj.treemanager.tree.node.service.TreeNodeChildSavingService;
import rj.treemanager.tree.node.service.TreeNodeMovingService;
import rj.treemanager.tree.node.service.TreeNodeService;
import rj.treemanager.tree.node.service.TreeNodeUpdateService;

/**
 * Controller for operations on specific tree nodes
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/tree/nodes")
public class TreeNodeController {

    private final TreeNodeService treeNodeService;
    private final TreeNodeChildSavingService treeNodeChildSavingService;
    private final TreeNodeUpdateService treeNodeUpdateService;
    private final TreeNodeMovingService treeNodeMovingService;

    @GetMapping("/{nodeId}")
    @Operation(summary = "Get node with given Id")
    public TreeNode getNode(@PathVariable Long nodeId) {
        return treeNodeService.getNode(nodeId);
    }

    @PatchMapping("/{nodeId}")
    @Operation(summary = "Update value of node with given Id")
    public void updateNode(
            @PathVariable Long nodeId,
            @RequestBody @JsonView(TreeNode.AddUpdateView.class) TreeNode treeNode
    ) {
        treeNodeUpdateService.update(nodeId, treeNode);
    }

    @DeleteMapping("/{nodeId}")
    @Operation(summary = "Delete node with given Id")
    public void deleteNode(@PathVariable Long nodeId) {
        treeNodeService.deleteNode(nodeId);
    }

    @PostMapping("/{nodeId}/addChild")
    @Operation(summary = "Add child to node with given Id")
    public void addChildNode(
            @PathVariable Long nodeId,
            @RequestBody @JsonView(TreeNode.AddUpdateView.class) TreeNode treeNode
    ) {
        treeNodeChildSavingService.saveChild(nodeId, treeNode);
    }

    @PostMapping("/{nodeId}/addChildWithChildren")
    @Operation(summary = "Add child with children to node with given Id")
    public void addChildNodeWithChildren(
            @PathVariable Long nodeId,
            @RequestBody @JsonView(TreeNode.AddWithChildrenView.class) TreeNode treeNode
    ) {
        treeNodeChildSavingService.saveChildWithChildrenForParentId(nodeId, treeNode);
    }

    @PostMapping("/{nodeId}/copyTo/{targetParentNodeId}")
    @Operation(summary = "Copy node with children to given parent node")
    public void copyNodeTo(@PathVariable Long nodeId, @PathVariable Long targetParentNodeId) {
        treeNodeMovingService.copyTo(nodeId, targetParentNodeId);
    }

    @PostMapping("/{nodeId}/moveTo/{targetParentNodeId}")
    @Operation(summary = "Move node with children to given parent node")
    public void moveNodeTo(@PathVariable Long nodeId, @PathVariable Long targetParentNodeId) {
        treeNodeMovingService.moveTo(nodeId, targetParentNodeId);
    }

}
