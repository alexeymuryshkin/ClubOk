import React, { Component } from 'react';

class Template extends Component {
    constructor(props) {
		super(props);
		this.state = {
			paramA: props.paramA,
			paramB: props.paramB,
			paramC: defaulValueC
		}

		this.classMethod1 = this.classMethod1.bind(this);
		this.classMethod2 = this.classMethod2.bind(this);
		this.changeStateMethod = this.changeStateMethod.bind(this);
	}

	static defaultProps = {
		paramA: defaultValueA,
		paramB: defaultValueB,
		paramC: defaultValueC
	}

	classMethod1() {}
	classMethod2() {}
	
	changeStateMethod(data) {
		this.setState( {
			paramA: data.paramA,
			paramB: data.paramB,
			paramC: data.paramC
        } );
	}

	render() {
    	return (
			<div className="AppBar">
                <h1>This is my <b>App Bar</b></h1>
		  	</div>
    	);
  	}
}

export default Template;