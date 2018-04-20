import React, {Component} from 'react'

import ServerRequest from "../ServerRequest";
import {Button, Form, Header, Segment} from "semantic-ui-react";

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
            <Segment>
                <Form>
                    <Header as='h2' textAlign='center'>Junk mail annoys you?</Header>
                    <Header as='h2' textAlign='center'>Just sign up!</Header>
                    <Form.Input fluid type='text' label='First Name' placeholder='First Name' onChange={this.onFNameChange}/>
                    <Form.Input fluid type='text' label='Last Name' placeholder='Last Name' onChange={this.onLNameChange}/>
                    <Form.Input fluid type='email' label='Email' placeholder='Email' onChange={this.onEmailChange}/>
                    <Form.Input fluid type='password' label='Password' placeholder='Password' onChange={this.onPasswordChange}/>
                    <Form.Input fluid type='password' label='Confirm Password' placeholder='Repeat password' onChange={this.onRePasswordChange}/>
                    <Button type='button' onClick={this.signUp}>Register</Button>
                </Form>
            </Segment>
        );
    }
}

export default SignUpComp