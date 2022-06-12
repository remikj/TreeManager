import {config} from "../config"

export class TreeClient {

    constructor() {
        this.baseUrl = config.BACKEND_URL
    }

    getTree() {
        return fetch(this.baseUrl + "/tree")
            .then(res => res.json())
    }

    updateNode(nodeId, newNode) {
        return fetch(this.baseUrl + "/tree/nodes/" + nodeId,
            {method: "PATCH", body: JSON.stringify(newNode), headers: {'Content-Type': 'application/json'}})
    }

    deleteNode(nodeId) {
        return fetch(this.baseUrl + "/tree/nodes/" + nodeId, {method: "DELETE"})
    }

    addChild(nodeId, childNode) {
        return fetch(this.baseUrl + "/tree/nodes/" + nodeId + "/addChild",
            {method: "POST", body: JSON.stringify(childNode), headers: {'Content-Type': 'application/json'}})
    }

    saveTree(tree) {
        return fetch(this.baseUrl + "/tree",
            {method: "PUT", body: JSON.stringify(tree), headers: {'Content-Type': 'application/json'}})
    }

    copy(nodeToCopyId, targetParentNodeId) {
        return fetch(this.baseUrl + "/tree/nodes/" + nodeToCopyId + "/copyTo/" + targetParentNodeId,
            {method: "POST"})
    }

    move(nodeToCopyId, targetParentNodeId) {
        return fetch(this.baseUrl+ "/tree/nodes/" + nodeToCopyId + "/moveTo/" + targetParentNodeId,
            {method: "POST"})
    }
}