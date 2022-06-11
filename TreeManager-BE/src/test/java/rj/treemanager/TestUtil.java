package rj.treemanager;

import rj.treemanager.tree.node.TreeNode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestUtil {

    public static TreeNode createDefaultRootNode() {
        return createNodeWithValueSumToRootAndChildNodes(0, 0, null);
    }

    public static TreeNode createNodeWithValueSumToRootAndChildNodes(long value, long sumToRoot, List<TreeNode> childNodes) {
        return new TreeNode(null, value, sumToRoot, null, childNodes);
    }

    public static void assertEqualTree(TreeNode expected, TreeNode actual) {
        assertEquals(expected.getValue(), actual.getValue());
        assertEquals(expected.getSumToRoot(), actual.getSumToRoot());
        if (expected.getChildNodes() != null) {
            assertNotNull(actual.getChildNodes());
            assertEquals(expected.getChildNodes().size(), actual.getChildNodes().size());
            for (var i = 0; i < expected.getChildNodes().size(); i++) {
                assertEqualTree(expected.getChildNodes().get(i), actual.getChildNodes().get(i));
            }
        }
    }
}
