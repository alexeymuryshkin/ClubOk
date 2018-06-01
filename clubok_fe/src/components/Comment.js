import React from 'react';
import {connect} from "react-redux";
import {Link} from "react-router-dom";
import {startRemoveComment} from "../actions/posts";

class Comment extends React.Component {
  state = {};

  onRemove = () => {
    this.props.startRemoveComment(this.props.postId, this.props.id)
  };

  render() {
    return (
      <div>
        <Link to={`/user/${this.props.user.id}`}>
          {this.props.user.fname}
        </Link>
        <p>{this.props.text}</p>
        <button onClick={this.onRemove}>Remove</button>
      </div>
    );
  };
}

const mapDispatchToProps = (dispatch) => ({
  startRemoveComment: (postId, commentId) => dispatch(startRemoveComment(postId, commentId))
});
    
export default connect(undefined, mapDispatchToProps)(Comment);