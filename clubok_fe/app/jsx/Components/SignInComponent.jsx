import React, {Component} from 'react'

import ServerRequest from "../ServerRequest";
import {Button, Form, Segment} from "semantic-ui-react";

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
            <Segment>
                <Form>
                    <Form.Input width='16' fluid type='email' label='Email' placeholder='example@email.com'/>
                    <Form.Input fluid type='password' label='Password' placeholder='Password'/>
                    <Button type='button' onClick={this.signIn}>Login</Button>
                </Form>
            </Segment>
        );
    }
}

export default SignInComponent