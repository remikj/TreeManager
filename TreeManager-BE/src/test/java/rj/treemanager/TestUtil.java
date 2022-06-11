package rj.treemanager;

import rj.treemanager.tree.node.TreeNode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestUtil {

    public static TreeNode createDefaultRootNode() {
        return createNodeWithValueSumToRootAndChildren(0, 0, null);
    }

    public static TreeNode createNodeWithValueSumToRootAndChildren(long value, long sumToRoot, List<TreeNode> children) {
        return new TreeNode(null, value, sumToRoot, null, children);
    }

    public static void assertEqualTree(TreeNode expected, TreeNode actual) {
        assertEquals(expected.getValue(), actual.getValue());
        assertEquals(expected.getSumToRoot(), actual.getSumToRoot());
        if (expected.getChildren() != null) {
            assertNotNull(actual.getChildren());
            assertEquals(expected.getChildren().size(), actual.getChildren().size());
            for (var i = 0; i < expected.getChildren().size(); i++) {
                assertEqualTree(expected.getChildren().get(i), actual.getChildren().get(i));
            }
        }
    }
}
