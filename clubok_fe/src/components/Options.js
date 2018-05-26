import React from 'react';
import {Link} from "react-router-dom";

const Options = () => (
  <div>
    <Link to={'/create/club'}>New Club</Link>
    <Link to={'/create/user'}>New User</Link>
    <Link to={'/create/post'}>New Post</Link>
    <Link to={'/create/event'}>New Event</Link>
  </div>
);

export default Options;