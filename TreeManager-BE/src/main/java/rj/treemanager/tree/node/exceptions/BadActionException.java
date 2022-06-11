package rj.treemanager.tree.node.exceptions;

public class BadActionException extends RuntimeException {

    public BadActionException(String msg) {
        super(msg);
    }

    public static BadActionException cannotDeleteRootNode() {
        return new BadActionException("Cannot delete root node");
    }

    public static BadActionException cannotMoveNodeToItsChild() {
        return new BadActionException("Cannot move node to itself or one of its child nodes");

    }
}
