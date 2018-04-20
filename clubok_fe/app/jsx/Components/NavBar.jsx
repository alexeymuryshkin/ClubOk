import React, {Component} from 'react';
import {Button, Container, Image, Menu} from 'semantic-ui-react';

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
            <Menu size='large'>
                <Container>
                    <Menu.Item header>
                        <Image size='mini' src='images/logo.png'/>
                        ClubOk
                    </Menu.Item>
                    <Menu.Menu position='right'>
                        <Menu.Item>
                            <Button negative onClick={this.signOut}>Log Out</Button>
                        </Menu.Item>
                    </Menu.Menu>
                </Container>
            </Menu>
        )
    }
}

export default NavBar;