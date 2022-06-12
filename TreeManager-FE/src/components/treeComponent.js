import React from "react";
import Tree from 'react-d3-tree';
import clone from "clone";
import {TreeClient} from "../services/treeClient";
import {TreeEditor} from "../services/treeEditor";
import {ValueForm} from "./valueComponent";

export class TreeComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            error: null, isLoaded: false, tree: {}, selectedNode: null, autosave: true
        };
        this.addedNodes = 0;
        this.treeClient = new TreeClient();
        this.treeEditor = new TreeEditor();
    }

    componentDidMount() {
        this.loadTree();
    }

    toggleAutosave = () => {
        let newAutosaveValue = !this.state.autosave
        if (newAutosaveValue && this.state.unprocessedChanges) { //save the tree if autosave turns on and there are unprocessed changes
            this.treeClient.saveTree(this.state.tree)
                .then(res => {
                    if (res.status === 200) {
                        this.loadTree()
                        this.setState({autosave: true})
                    } else {
                        res.text().then(text => alert("Cannot save changes: " + text))
                    }
                })
        } else {
            this.setState({
                autosave: newAutosaveValue
            })
        }
    }

    saveTree = () => {
        if (this.state.autosave) {
            return
        }
        this.treeClient.saveTree(this.state.tree)
            .then(res => {
                if (res.status === 200) {
                    this.loadTree()
                } else {
                    res.text().then(text => alert("Cannot save changes: " + text))
                }
            })
    }

    loadTree = () => {
        this.treeClient.getTree()
            .then((result) => {
                this.setState({
                    isLoaded: true, selectedNode: null, tree: result, unprocessedChanges: false
                });
            }, (error) => {
                this.setState({
                    isLoaded: true, error
                });
            })
    }

    addChildNode = (childValue) => {
        if (this.state.autosave) {
            this.treeClient.addChild(this.state.selectedNode.id, {value: childValue})
                .then(() => this.loadTree())
        } else {
            const nextTree = clone(this.state.tree);
            let childNode = {id: 'added-id-' + this.addedNodes++, value: childValue, children: [], unprocessed: true}
            this.treeEditor.addChild(nextTree, this.state.selectedNode.id, childNode)
            this.setState({
                isLoaded: true,
                tree: nextTree,
                unprocessedChanges: true
            });
        }
    };

    updateValue = (newValue) => {
        if (this.state.autosave) {
            this.treeClient.updateNode(this.state.selectedNode.id, {value: newValue})
                .then(res => {
                    this.loadTree()
                    if (res.status !== 200) {
                        res.text().then(text => alert(text))
                    }
                })
        } else {
            const nextTree = clone(this.state.tree);
            this.treeEditor.updateValueAndMarkedUnprocessed(nextTree, this.state.selectedNode.id, newValue)
            this.setState({
                tree: nextTree, selectedNode: null, unprocessedChanges: true
            });
        }
    };

    deleteNode = () => {
        if (this.state.autosave) {
            this.treeClient.deleteNode(this.state.selectedNode.id)
                .then(res => {
                    this.loadTree()
                    if (res.status !== 200) {
                        res.text().then(text => alert(text))
                    }
                })
        } else {
            const nextTree = clone(this.state.tree);
            this.treeEditor.removeElement(nextTree, this.state.selectedNode.id)
            this.setState({
                tree: nextTree, selectedNode: null, unprocessedChanges: true
            });
        }
    };

    onNodeClick = (clicked) => {
        let previous = clone(this.state.selectedNode)
        if (previous == null || !(previous.copying || previous.moving)) {
            this.setState({selectedNode: clicked.data})
        } else {
            this.handleCopyOrMove(previous, clicked.data)
        }
    }

    startNodeCopy = () => {
        let selectedNode = clone(this.state.selectedNode)
        selectedNode.copying = true
        selectedNode.moving = false
        this.setState({selectedNode: selectedNode})
    }

    startNodeMove = () => {
        let selectedNode = clone(this.state.selectedNode)
        selectedNode.copying = false
        selectedNode.moving = true
        this.setState({selectedNode: selectedNode})
    }

    handleCopyOrMove(previous, clicked) {
        if (previous.copying) {
            this.handleCopy(previous, clicked)
        }
        if (previous.moving) {
            this.handleMove(previous, clicked)
        }
    }

    handleCopy(previous, clicked) {
        if (this.state.autosave) {
            this.treeClient.copy(previous.id, clicked.id)
                .then(res => {
                    this.loadTree()
                    if (res.status !== 200) {
                        res.text().then(text => alert(text))
                    }
                })
        } else {
            let nextTree = clone(this.state.tree)
            this.treeEditor.copy(nextTree, previous.id, clicked.id)
            this.setState({tree: nextTree, unprocessedChanges: true, selectedNode: null})
        }
    }

    handleMove(previous, clicked) {
        if (this.state.autosave) {
            this.treeClient.move(previous.id, clicked.id)
                .then(res => {
                    this.loadTree()
                    if (res.status !== 200) {
                        res.text().then(text => alert(text))
                    }
                })
        } else {
            let nextTree = clone(this.state.tree)
            let err = this.treeEditor.move(nextTree, previous.id, clicked.id)
            if(err) alert(err)
            this.setState({tree: nextTree, unprocessedChanges: true, selectedNode: null})
        }
    }

    cancelCopyMove = () => {
        let selectedNode = clone(this.state.selectedNode)
        selectedNode.copying = false
        selectedNode.moving = false
        this.setState({selectedNode: selectedNode})
    }

    render() {
        const {error, isLoaded, tree, selectedNode, autosave} = this.state;
        const foreignObjectProps = {width: 500, height: 20, x: 20, y: -10};
        if (error) {
            return <div>Error: {error.message}</div>;
        } else if (!isLoaded) {
            return <div>Loading...</div>;
        } else {
            return (<div style={{width: '100vw', height: '100vh', display: 'inline-flex'}}>
                {(selectedNode && (selectedNode.copying || selectedNode.moving)) &&
                    <div style={{zIndex: 5, position: 'absolute', textAlign: "center", width: "100%"}}>
                        {(selectedNode.copying) && <span>COPYING NODE - Select target node</span>}
                        {(selectedNode.moving) && <span>MOVING NODE - Select target node</span>}
                        <button onClick={this.cancelCopyMove}>Cancel</button>
                    </div>}
                <div style={{width: '100vw', height: '100vh'}}>
                    <div id="treeWrapper" style={{width: '100%', height: '100%', position: 'absolute'}}>
                        <Tree data={tree}
                              translate={{ x: window.innerWidth / 5, y: window.innerHeight / 2 }}
                              onNodeClick={this.onNodeClick}
                              collapsible={false}
                              renderCustomNodeElement={(rd3tProps) => this.renderForeignObjectNode({
                                  ...rd3tProps,
                                  foreignObjectProps
                              })}
                        />
                    </div>
                </div>
                <div style={{
                    minWidth: "200px",
                    width: '15%',
                    alignContent: 'center',
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'space-between',
                    zIndex: 1
                }}>
                    <div>
                        {selectedNode && <div style={{
                            border: '5',
                            borderColor: 'gray',
                            borderStyle: 'solid',
                            borderRadius: `10px`,
                            background: 'lightgray',
                            margin: '20px'
                        }}>Selected Node<br/>
                            Value: {selectedNode.value}<br/>
                            SumToRoot: {selectedNode.sumToRoot}<br/>
                            {selectedNode.unprocessed && <span>Unsaved changes</span>}
                            <ValueForm processValue={this.updateValue.bind(this)}
                                       buttonText={"Update Node Value"}></ValueForm>
                            <ValueForm processValue={this.addChildNode.bind(this)}
                                       buttonText={"Add Child Node"}></ValueForm>
                            <button onClick={this.deleteNode}>Delete Node</button>
                            <button onClick={this.startNodeCopy}>Copy Node</button>
                            <button onClick={this.startNodeMove}>Move Node</button>
                        </div>}
                    </div>
                    <div>
                        <button onClick={this.loadTree}>Reload</button>
                        <br/>
                        <button onClick={this.saveTree}>Save Tree</button>
                        <br/>
                        <label>
                            <input
                                type="checkbox"
                                checked={autosave}
                                onChange={this.toggleAutosave}
                            />
                            Save Automatically
                        </label>
                    </div>
                </div>
            </div>);
        }
    }

    renderForeignObjectNode = ({
                                   nodeDatum, onNodeClick, foreignObjectProps
                               }) => (
        <g>
            <circle r={15} fill={this.getColor(nodeDatum)} onClick={onNodeClick}></circle>
            }
            <foreignObject {...foreignObjectProps}>
                {(nodeDatum.children == null || !nodeDatum.children.length) &&
                    <div style={{textAlign: "left"}}>{nodeDatum.sumToRoot}</div>}
            </foreignObject>
        </g>
    )

    getColor(nodeDatum) {
        if (this.state.selectedNode != null && this.state.selectedNode.id === nodeDatum.id) {
            return "cyan"
        } else if (nodeDatum.unprocessed) {
            return "red"
        } else {
            return (nodeDatum.children != null && nodeDatum.children.length) ? "white" : "green"
        }
    }
}