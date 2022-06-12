import clone from "clone";

export class TreeEditor {
    constructor() {
        this.copied = 0;
    }

    addChild(tree, parentId, child) {
        let parentNode = this.findElement(tree, parentId)
        if (parentNode == null)
            return
        this.addChildToNode(parentNode, child);
    }

    updateValueAndMarkedUnprocessed(tree, nodeId, newValue) {
        let node = this.findElement(tree, nodeId)
        node.value = newValue
        this.markUnprocessed(node)
    }

    removeElement(tree, elementId) {
        let [parentNode, idx] = this.findParentOf(tree, elementId)
        if (idx === -1)
            return
        parentNode.unprocessed = true
        parentNode.children.splice(idx, 1)
    }

    copy(tree, nodeToCopyId, targetParentNodeId) {
        let nodeToCopy = this.findElement(tree, nodeToCopyId)
        let targetParentNode = this.findElement(tree, targetParentNodeId)
        if (nodeToCopy == null || targetParentNode == null) {
            return
        }
        let nodeCopy = clone(nodeToCopy)
        this.markCopied(nodeCopy)
        this.addChildToNode(targetParentNode, nodeCopy)
    }

    move(tree, nodeToMoveId, targetParentNodeId) {
        let [nodeToMoveParent, nodeToMoveIdx] = this.findParentOf(tree, nodeToMoveId)
        let targetParentNode = this.findElement(tree, targetParentNodeId)
        if (nodeToMoveIdx === -1 || targetParentNode == null) {
            return
        }
        let nodeToMove = nodeToMoveParent.children[nodeToMoveIdx]
        let targetInNodeToMove = this.findElement(nodeToMove, targetParentNodeId) // check if node isn't moved to itself
        if (targetInNodeToMove != null) {
            return "Cannot move node to itself or its children"
        }
        this.addChildToNode(targetParentNode, nodeToMove)
        this.markCopied(nodeToMove)
        nodeToMoveParent.children.splice(nodeToMoveIdx, 1)
    }

    markUnprocessed(node) {
        node.unprocessed = true
        node.sumToRoot = null
        if (node.children === null) {
            return
        }
        for (let childNode of node.children) {
            this.markUnprocessed(childNode)
        }
    }

    markCopied(nodeCopy) {
        nodeCopy.id = nodeCopy.id + "-copied-" + this.copied++
        nodeCopy.unprocessed = true
        nodeCopy.sumToRoot = null
        if (nodeCopy.children === null) {
            return
        }
        for (let childNode of nodeCopy.children) {
            this.markCopied(childNode)
        }
    }

    addChildToNode(parentNode, child) {
        if (parentNode.children) {
            parentNode.children.push(child)
        } else {
            parentNode.children = [child]
        }
    }

    findElement(node, elementId) {
        if (node.id === elementId) {
            return node
        }
        let [parentNode, idx] = this.findParentOf(node, elementId)
        if (idx === -1)
            return null
        return parentNode.children[idx]
    }

    findParentOf(node, elementId) {
        if (node == null || node.id === elementId) {
            return [null, -1]
        }
        if (node.children === null) {
            return [null, -1]
        }

        // check if current node is parent of searched element
        for (let i = 0; i < node.children.length; i++) {
            if (node.children[i].id === elementId) {
                return [node, i]
            }
        }

        // check if child node is parent of searched element
        let parentNode
        let idx
        for (let i = 0; i < node.children.length; i++) {
            [parentNode, idx] = this.findParentOf(node.children[i], elementId)
            if (parentNode != null) {
                return [parentNode, idx]
            }
        }
        return [null, -1]
    }
}