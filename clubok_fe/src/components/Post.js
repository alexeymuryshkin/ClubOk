import React from 'react';
import {Link} from "react-router-dom";

const Post = (props) => {
  return (
    <div>
      <Link to={`/club/${props.link}`}>
        {props.club.name}
      </Link>
      <p>{props.body}</p>
    </div>
  );
};
    
export default Post;