import React from 'react';
import {connect} from "react-redux";
import {Link} from "react-router-dom";
import moment from "moment";
import {startCommentPost, startLikePost} from "../actions/posts";
import Comment from "./Comment";

export class Post extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      likes: props.likes,
      comments: props.comments,
      commentText: '',
      error: ''
    };
  }

  onCommentChange = (e) => {
    const commentText = e.target.value.trim();
    this.setState(() => ({commentText}));
  };

  onLike = () => {
    this.props.startLikePost(this.props.id);
  };

  onCommentSubmit = (e) => {
    e.preventDefault();
    if (!this.state.commentText) {
      this.setState(() => ({error: 'Enter comment'}));
    } else {
      this.setState(() => ({error: ''}));
      this.props.startCommentPost(this.props.id, {text: this.state.commentText});
      this.setState(() => ({text: ''}));
    }
  };

  render() {
    return (
      <div>
        <div>
          <Link to={`/club/${this.props.club.link}`}>
            <p>{this.props.club.name}</p>
          </Link>
          <p>{moment(this.props.postedAt * 1000).fromNow()}</p>
        </div>
        <div>
          <p>{this.props.body}</p>
        </div>
        <div>
          <div>
            <p>{this.props.likes.length}</p>
            <button onClick={this.onLike}>Like</button>
          </div>
        </div>
        <div>
          {this.props.comments.map((comment) => (
            <Comment key={comment.id} postId={this.props.id} {...comment}/>
          ))}
        </div>
        <div>
          <form onSubmit={this.onCommentSubmit}>
            <input
              type="text"
              value={this.state.commentText}
              placeholder="Add Comment..."
              onChange={this.onCommentChange}
            />
            <button>Send</button>
          </form>
        </div>
      </div>
    );
  }
}

const mapDispatchToProps = (dispatch) => ({
  startLikePost: (postId) => dispatch(startLikePost(postId)),
  startCommentPost: (postId, comment) => dispatch(startCommentPost(postId, comment))
});
    
export default connect(undefined, mapDispatchToProps)(Post);