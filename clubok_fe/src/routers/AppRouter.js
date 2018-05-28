import React from 'react';
import {BrowserRouter, Route, Switch} from "react-router-dom";
import FeedPage from "../components/FeedPage";
import AuthorizationPage from "../components/AuthorizationPage";
import AddUserPage from "../components/AddUserPage";
import AddPostPage from "../components/AddPostPage";
import AddClubPage from "../components/AddClubPage";
import NotFoundPage from "../components/NotFoundPage";
import Options from "../components/Options";
// import AddEvent from "../components/AddEvent";

const AppRouter = () => (
  <BrowserRouter>
    <Options/>
    <Switch>
      <Route path="/" component={FeedPage} exact={true}/>
      <Route path="/create/user" component={AddUserPage}/>
      <Route path="/create/club" component={AddClubPage}/>
      <Route path="/create/post" component={AddPostPage}/>
      {/*<Route path="/create/event" component={AddEvent}/>*/}
      <Route path="/signup" component={AuthorizationPage}/>
      <Route component={NotFoundPage}/>
    </Switch>
  </BrowserRouter>
);

export default AppRouter;