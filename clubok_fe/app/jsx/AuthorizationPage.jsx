import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import {Redirect, Router} from 'react-router-dom';

import '../less/AuthorizationPage.less';
import ServerRequest from "./ServerRequest";
import SignInComponent from "./Components/SignInComponent";
import SignUpComp from "./Components/SignUpComp";

class AuthorizationPage extends Component{

    constructor(props) {
        super(props);
        console.log('Hi, it is Authorization Page!)');
        console.log(ServerRequest.getInstance().token);
        this.renderRedirect = this.renderRedirect.bind(this);

        if (ServerRequest.getInstance().token != null) {
            window.location.replace("http://localhost:3000/feed.html");
        }
    }

    renderRedirect () {
        if (ServerRequest.getInstance().token) {
            return <Redirect to="/feed.html"/>;
        }
    }

    render() {
        return (
            <div>
                {/*this.renderRedirect()*/}
                <div className="topnav">
                    <button className="btn">ClubOK</button>
                    <input type="text" className="search" placeholder="Search"/>
                </div>


                <div className="row">
                    <div className="column">
                        <div className="image">image?</div>
                    </div>

                    <div className="column">
                        <SignInComponent/>
                        <SignUpComp/>
                    </div>

                </div>

                <hr/>
                <div className="footer">
                    <div className="info-about">
                        <a href="#about">ClubOK</a>
                        2018
                    </div>
                    <div className="info-team">
                        <a href="#about">Our team</a>
                    </div>
                </div>
            </div>
        );
    }
}

ReactDOM.render(
    <AuthorizationPage/>,
    document.getElementById('app')
);

export default AuthorizationPage

