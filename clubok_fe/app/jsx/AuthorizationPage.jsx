import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import {Redirect} from 'react-router-dom';

import ServerRequest from "./ServerRequest";
import SignInComponent from "./Components/SignInComponent";
import SignUpComp from "./Components/SignUpComp";
import NavBar from "./Components/NavBar";
import {Grid, Image} from "semantic-ui-react";

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
                <NavBar/>

                <Grid container>
                    <Grid.Row>
                        <Grid.Column width='10'>
                            <Image src='images/logo.png' size='large' floated='left'/>
                        </Grid.Column>
                        <Grid.Column width='6'>
                            <SignInComponent/>
                            <SignUpComp/>
                        </Grid.Column>
                    </Grid.Row>
                </Grid>

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

