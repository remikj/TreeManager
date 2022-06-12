import React from "react";

export class ValueForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {value: 0};
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
        this.setState({value: event.target.value});
    }

    handleSubmit(event) {
        this.props.processValue(this.state.value)
        event.preventDefault();
    }

    render() {
        return (
            <div style={{
                border: '3px',
                borderStyle: 'solid',
                borderRadius: '10px',
                borderColor: 'gray',
                margin: '5px'
            }}>
                <form onSubmit={this.handleSubmit}>
                    <label>
                        Value: <input type="text" pattern="[-]?[0-9]*" value={this.state.value} onChange={this.handleChange}
                                      title="Must be a number"
                                      style={{
                                          width: '50%',
                                          textAlign: 'right'
                                      }}/>
                    </label>
                    <input type="submit" value={this.props.buttonText}/>
                </form>
            </div>

        );
    }
}