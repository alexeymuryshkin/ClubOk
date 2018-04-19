import React, {Component} from 'react'

import '../less/AuthorizationPage.less'
import ServerRequest from "./ServerRequest";
import SignInComponent from "./Components/SignInComponent";
import SignUpComp from "./Components/SignUpComp";

class AuthorizationPage extends Component{

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div>
                <div className="topnav">
                    <button className="btn">ClubOK</button>
                    <input type="text" className="search" placeholder="Search"/>
                </div>


                <div className="row">
                    <div className="column"> <!-- style="background-color:lightgreen;" -->
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
        )
    }
}

export default AuthorizationPage