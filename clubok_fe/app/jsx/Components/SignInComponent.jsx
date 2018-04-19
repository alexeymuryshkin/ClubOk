import React, {Component} from 'react'

import '../../less/AuthorizationPage.less'
import ServerRequest from "../ServerRequest";

class SignInComponent extends Component{

    constructor() {
        super();
        //this.state = {};

        this.signIn = this.signIn.bind(this);
        this.onEmailChange = this.onEmailChange.bind(this);
        this.onPasswordChange = this.onPasswordChange.bind(this);
    }

    onEmailChange(e) {
        this.setState( {
            email: e.target.value
        } );
    }

    onPasswordChange(e) {
        this.setState({
            password: e.target.value
        });
    }

    signIn(event) {
        event.preventDefault();
        let data = {
            email: this.state.email,
            password: this.state.password
        };
        console.log(data);
        ServerRequest.getInstance().signIn(data);
    }

    render() {
        return (
            <form id='login' method='post' >
                <input type="email" placeholder="Email" onChange={this.onEmailChange} required/>
                <input type="password" placeholder="Password" onChange={this.onPasswordChange} required/>
                <button type='button' onClick={this.signIn}>Login</button>
            </form>
        );
    }
}

export default SignInComponent