import React, {Component} from 'react'

import '../../less/AuthorizationPage.less';
import ServerRequest from "../ServerRequest";

class SignUpComp extends Component{

    constructor() {
        super();
        //this.state = {};

        this.signUp = this.signUp.bind(this);
        this.onFNameChange = this.onFNameChange.bind(this);
        this.onLNameChange = this.onLNameChange.bind(this);
        this.onEmailChange = this.onEmailChange.bind(this);
        this.onPasswordChange = this.onPasswordChange.bind(this);
        this.onRePasswordChange = this.onRePasswordChange.bind(this);
    }

    onFNameChange(e) {
        this.setState({
            fname: e.target.value
        });
    }

    onLNameChange(e) {
        this.setState({
            lname: e.target.value
        });
    }

    onEmailChange(e) {
        this.setState({
            email: e.target.value
        });
    }

    onPasswordChange(e) {
        this.setState({
            password: e.target.value
        });
    }

    onRePasswordChange(e) {
        this.setState({
            repassword: e.target.value
        });
    }

    signUp(event) {
        event.preventDefault();
        let data = {
            email: this.state.email,
            password: this.state.password,
            fname: this.state.fname,
            lname: this.state.lname
        };
        console.log(data);
        if (this.state.password === this.state.repassword)
            ServerRequest.getInstance().signUp(data);
        else
            console.log("Passwords are not the same!");
    }

    render() {
        return (
            <form id='register' method='post' >
                <h2 className="reg-ques">Junk mail annoys you?</h2>
                <div className="reg-ans">Just, sign up!</div>
                <input type="text" placeholder="First Name" onChange={this.onFNameChange} required/>
                <input type="text" placeholder="Last Name" onChange={this.onLNameChange} required/>
                <input type="email" placeholder="Email" onChange={this.onEmailChange} required/>
                <input type="password" placeholder="Password" onChange={this.onPasswordChange} required/>
                <input type="password" placeholder="Re Password" onChange={this.onRePasswordChange} required/>
                <button type='button' onClick={this.signUp}>Register</button>
            </form>
        );
    }
}

export default SignUpComp