package rj.treemanager.tree;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import rj.treemanager.tree.node.TreeNode;
import rj.treemanager.tree.node.root.RootNode;

/**
 * Controller for operations on the entire tree
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/tree")
public class TreeController {

    private final RootNode rootNode;

    @GetMapping
    @Operation(summary = "Get whole tree structure")
    public TreeNode getTree() {
        return rootNode.getRootNode();
    }

    @PutMapping
    @Operation(summary = "Override whole tree with tree from request body")
    public void overrideTree(@RequestBody TreeNode newRootNode) {
        rootNode.overrideRootNode(newRootNode);
    }

    @DeleteMapping
    @Operation(summary = "Reset tree to single node with 0 value")
    public void resetTree() {
        rootNode.resetRootNode();
    }
}
