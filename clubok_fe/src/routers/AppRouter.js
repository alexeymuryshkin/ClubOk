import React from 'react';
import {BrowserRouter, Route, Switch} from "react-router-dom";
import FeedPage from "../components/FeedPage";
import SignUpPage from "../components/SIgnUpPage";
import NavBar from "../components/NavBar";

const AppRouter = () => (
  <BrowserRouter>
    <div>
      <NavBar/>
      <Switch>
        <Route path="/" component={FeedPage} exact={true}/>
        <Route path="/signup" component={SignUpPage}/>
      </Switch>
    </div>
  </BrowserRouter>
);

export default AppRouter;