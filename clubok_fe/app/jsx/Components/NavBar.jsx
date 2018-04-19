import React, { Component } from 'react';

import '../../less/feed.less';
import ServerRequest from "../ServerRequest";

class NavBar extends Component {
    constructor(props) {
        super(props);
        //this.state = {}
        this.onSearchChange = this.onSearchChange.bind(this);
        this.openMainPage = this.openMainPage.bind(this);
    }

    onSearchChange(e) {
        let text = e.target.value;
        console.log(text);
    }

    openMainPage() {
        console.log("Opening of The Main Page");
        window.location.replace("http://localhost:3000/");
    }

    signOut() {
        ServerRequest.getInstance().signOut();
    }

    render() {
        return (
            <div className="navbar">
                <button className="btn" onClick={this.openMainPage}>ClubOK</button>
                <input type="text" className="search" onChange={this.onSearchChange} placeholder="Search" />
                <button style={{float: "right"}} onClick={this.signOut}>Sign Out</button>
            </div>
        );
    }
}

export default NavBar;