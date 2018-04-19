import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import { Redirect } from 'react-router-dom';

//import '../less/FeedPage.less';
import ServerRequest from "./ServerRequest";

class FeedPage extends Component{

    constructor(props) {
        super(props);
        console.log('Hi, it is Feed Page!)');
        console.log(ServerRequest.getInstance().token);
        this.renderRedirect = this.renderRedirect.bind(this)
    }

    renderRedirect () {
        if (!ServerRequest.getInstance().token) {
            return <Redirect to="/" />;
        }
    }

    render() {
        return (
            <div>
                {this.renderRedirect()}
                Hello, Anuar!)
            </div>
        );
    }
}

ReactDOM.render(
    <FeedPage/>,
    document.getElementById('feed')
);

export default FeedPage

