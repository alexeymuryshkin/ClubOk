import React, {Component} from 'react';
import {Header, Menu} from 'semantic-ui-react';

class LeftNavBar extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <Menu vertical fluid>
                <Menu.Item>
                    <Header as='h4'>Student Government</Header>
                    <p>Recruitment</p>
                </Menu.Item>
            </Menu>
        );
    }
}

export default LeftNavBar;