import React, {Component} from 'react'

import '../less/AuthorizationPage.less'
import ServerRequest from "./ServerRequest";

class SignInComponent extends Component{

    constructor() {
        super();
        this.signIn = this.signIn.bind(this);
    }

    signIn(event) {
        event.preventDefault();
        const data = new FormData(event.target);
        for (entry in data.entries())
            console.log(entry);
        console.log(data.entries());
        //ServerRequest.getInstance().signIn();
    }

    render() {
        return (
            <form id='login' action="" method='post'>
                <input type="text" placeholder="Username" required/>
                <input type="password" placeholder="Password" required/>
                <button type='submit'>Login</button>
            </form>
        );
    }
}

export default SignInComponent