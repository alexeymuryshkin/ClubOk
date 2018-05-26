import React from 'react';
import {connect} from 'react-redux';
import Post from "./Post";


export const MainFeed = (props) => {
  return (
    <div>
      {props.posts.map((post) => (
        <Post key={post.id} {...post}/>
      ))}
    </div>
  );
};

const mapStateToProps = (state) => ({
  posts: state.posts
});

export default connect(mapStateToProps)(MainFeed);