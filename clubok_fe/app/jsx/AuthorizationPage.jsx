import React, {Component} from 'react'

import '../less/AuthorizationPage.less'
import ServerRequest from "./ServerRequest";

class AuthorizationPage extends Component{

    constructor() {
        super();

        this.signUp = this.signUp.bind(this);
        this.signIn = this.signIn.bind(this);
    }

    signUp(event) {
        event.preventDefault();
        const data = new FormData(event.target);
        console.log(data.entries());
        //ServerRequest.getInstance().signUp(data.get());
    }

    signIn(event) {
        event.preventDefault();
        const data = new FormData(event.target);
        console.log(data.entries());
        //ServerRequest.getInstance().signIn();
    }

    render() {
        return (
            <div class="form">

                <ul class="tab-group">
                    <li class="tab active"><a href="#signup">Sign Up</a></li>
                    <li class="tab"><a href="#login">Log In</a></li>
                </ul>

                <div class="tab-content">
                    <div id="signup">
                        <h1>Sign Up Form</h1>

                        <form onSubmit={this.signUp}>

                            <div class="top-row">
                                <div class="field-wrap">
                                    <label>
                                        First Name<span class="req">*</span>
                                    </label>
                                    <input type="text" required autocomplete="off" />
                                </div>

                                <div class="field-wrap">
                                    <label>
                                        Last Name<span class="req">*</span>
                                    </label>
                                    <input type="text"required autocomplete="off"/>
                                </div>
                            </div>

                            <div class="field-wrap">
                                <label>
                                    Email Address<span class="req">*</span>
                                </label>
                                <input type="email"required autocomplete="off"/>
                            </div>

                            <div class="field-wrap">
                                <label>
                                    Set A Password<span class="req">*</span>
                                </label>
                                <input type="password"required autocomplete="off"/>
                            </div>

                            <button type="submit" class="button button-block">Get Started</button>

                        </form>

                    </div>

                    <div id="login">
                        <h1>Welcome Back!</h1>

                        <form onSubmit={this.signIn}>

                            <div class="field-wrap">
                                <label>
                                    Email Address<span class="req">*</span>
                                </label>
                                <input type="email"required autocomplete="off"/>
                            </div>

                            <div class="field-wrap">
                                <label>
                                    Password<span class="req">*</span>
                                </label>
                                <input type="password"required autocomplete="off"/>
                            </div>

                            <p class="forgot"><a href="#">Forgot Password?</a></p>

                            <button class="button button-block">Log In</button>

                        </form>

                    </div>

                </div>
            </div>
        )
    }
}

export default AuthorizationPage