import React, {Component} from 'react';
import ReactDOM from 'react-dom';

import AppBar from './Components/AppBar';
import SideBar from './Components/SideBar';
import MainView from './Components/MainView';

import ServerRequest from './ServerRequest';

import Hello from './Hello.jsx';

import '../less/app.less';

class App extends Component {
    // constructor() {
    // 	super();
    //
    // 	this.state = {
    //        server_request: ServerRequest.sharedInstance 
    // 	}
    // }

    render() {
        return (
            <div className="App">
                <h1>Hello World!</h1>
                <Hello who="Anuar"/>
                <AppBar width='100%'/>
                <div>
                    {/*<SideBar float='left' overflow='hidden'/>*/}
                    {/*<MainView float='left' overflow='hidden'/>*/}
                </div>
            </div>
        );
    }
}

ReactDOM.render(
    <App/>,
    document.getElementById('app')
);