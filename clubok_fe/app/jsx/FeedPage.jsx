import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import {Redirect, Router} from 'react-router-dom';

import '../less/feed.less';
import ServerRequest from "./ServerRequest";
import NavBar from "./Components/NavBar";

class FeedPage extends Component{

    constructor(props) {
        super(props);
        console.log('Hi, it is Feed Page!)');
        console.log(window.sessionStorage.getItem('token'));
        this.renderRedirect = this.renderRedirect.bind(this);

        if (!window.sessionStorage.getItem('token')) {
            window.location.replace("http://localhost:3000/");
        }
    }

    renderRedirect () {
        if (!window.sessionStorage.getItem('token')) {
            return <Redirect to="/" />;
        }
    }

    render() {
        return (
            <div>
                {/*this.renderRedirect()*/}
                <NavBar />
                {/*<div className="row">
                    <LeftNavBar />
                    <MainView />
                    <RightNavBar />
                </div>
                <Footer />*/}
            </div>
        );
    }
}

ReactDOM.render(
    <FeedPage/>,
    document.getElementById('feed')
);

export default FeedPage

