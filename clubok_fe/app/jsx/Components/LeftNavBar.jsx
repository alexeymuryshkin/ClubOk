import React, {Component} from 'react';
import {Menu} from 'semantic-ui-react';

class LeftNavBar extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <Menu vertical fluid>
                <Menu.Item name='Home' link='#' active/>
                <Menu.Item name='Clubs' link='#'/>
                <Menu.Item name='About Us' link='#'/>
            </Menu>
        );
    }
}

export default LeftNavBar;