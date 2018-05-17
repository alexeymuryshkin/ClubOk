import React from 'react';
import {BrowserRouter, Route} from "react-router-dom";
import FeedPage from "../components/FeedPage";
import SignUpPage from "../components/SIgnUpPage";

const AppRouter = () => (
  <BrowserRouter>
    <div>
      <Route path="/" component={FeedPage} exact={true}/>
      <Route path="/signup" component={SignUpPage}/>
    </div>
  </BrowserRouter>
);

export default AppRouter;