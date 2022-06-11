package rj.treemanager.tree.node.root;

public class RootNodeException extends RuntimeException{
    public RootNodeException(String msg) {
        super(msg);
    }

    public static RootNodeException tooManyRootNodes() {
        return new RootNodeException("Multiple root nodes in repository");
    }
}
