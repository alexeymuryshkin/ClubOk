import React from 'react';
import {BrowserRouter, Route, Switch} from "react-router-dom";
import FeedPage from "../components/FeedPage";
import SignUpPage from "../components/SIgnUpPage";
import AddUser from "../components/AddUser";
import AddClub from "../components/AddClub";
import AddPost from "../components/AddPost";
import AddEvent from "../components/AddEvent";

const AppRouter = () => (
  <BrowserRouter>
    <Switch>
      <Route path="/" component={FeedPage} exact={true}/>
      <Route path="/create/user" component={AddUser}/>
      <Route path="/create/club" component={AddClub}/>
      <Route path="/create/post" component={AddPost}/>
      <Route path="/create/event" component={AddEvent}/>
      <Route path="/signup" component={SignUpPage}/>
    </Switch>
  </BrowserRouter>
);

export default AppRouter;