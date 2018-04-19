import React, {Component} from 'react'

import '../less/AuthorizationPage.less'
import ServerRequest from "./ServerRequest";

class SignUpComp extends Component{

    constructor() {
        super();
        this.signUp = this.signUp.bind(this);
    }

    signUp(event) {
        event.preventDefault();
        const data = new FormData(event.target);
        console.log(data.entries());
        //ServerRequest.getInstance().signUp(data.get());
    }

    render() {
        return (
            <form id='register' onsubmit={signUp} method='post'>
                <!-- <b>Junk mail annoys you?<b> -->
                <h2 className="reg-ques">Junk mail annoys you?</h2>
                <div className="reg-ans">Just, sign up!</div>
                <input type="text" placeholder="Username" required/>
                <input type="email" placeholder="Email" required/>
                <input type="password" placeholder="Password" required/>
                <input type="password" placeholder="Re Password" required/>
                <button type='submit'>Register</button>
            </form>
        );
    }
}

export default SignUpComp