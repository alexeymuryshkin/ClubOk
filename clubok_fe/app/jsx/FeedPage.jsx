import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import {Redirect} from 'react-router-dom';
import {Grid} from 'semantic-ui-react';

import NavBar from "./Components/NavBar";
import Post from "./Components/Post";
import LeftNavBar from "./Components/LeftNavBar";
import RightNavBar from "./Components/RightNavBar";

class FeedPage extends Component{

    constructor(props) {
        super(props);
        console.log('Hi, it is Feed Page!)');
        console.log(window.sessionStorage.getItem('token'));
        this.renderRedirect = this.renderRedirect.bind(this);

        if (window.sessionStorage.getItem('token') == null) {
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
                <Grid container textAlign='justified'>
                    <Grid.Row>
                        <Grid.Column width='4'>
                            <LeftNavBar/>
                        </Grid.Column>
                        <Grid.Column width='8'>
                            <Post/>
                        </Grid.Column>
                        <Grid.Column width='4'>
                            <RightNavBar/>
                        </Grid.Column>
                    </Grid.Row>
                </Grid>
            </div>
        );
    }
}

ReactDOM.render(
    <FeedPage/>,
    document.getElementById('feed')
);

