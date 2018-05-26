import React from 'react';
import {BrowserRouter, Route, Switch} from "react-router-dom";
import FeedPage from "../components/FeedPage";
import SignUpPage from "../components/SIgnUpPage";

const AppRouter = () => (
  <BrowserRouter>
    <Switch>
      <Route path="/" component={FeedPage} exact={true}/>
      <Route path="/signup" component={SignUpPage}/>
    </Switch>
  </BrowserRouter>
);

export default AppRouter;