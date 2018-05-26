import React from 'react';
import {BrowserRouter, Route, Switch} from "react-router-dom";
import FeedPage from "../components/FeedPage";
import SignUpPage from "../components/SIgnUpPage";
import AddUserPage from "../components/AddUserPage";
// import AddClub from "../components/AddClub";
import AddPostPage from "../components/AddPostPage";
// import AddEvent from "../components/AddEvent";

const AppRouter = () => (
  <BrowserRouter>
    <Switch>
      <Route path="/" component={FeedPage} exact={true}/>
      <Route path="/create/user" component={AddUserPage}/>
      {/*<Route path="/create/club" component={AddClub}/>*/}
      <Route path="/create/post" component={AddPostPage}/>
      {/*<Route path="/create/event" component={AddEvent}/>*/}
      <Route path="/signup" component={SignUpPage}/>
    </Switch>
  </BrowserRouter>
);

export default AppRouter;