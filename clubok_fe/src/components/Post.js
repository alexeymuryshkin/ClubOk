import React from 'react';
import {connect} from "react-redux";
import {Link} from "react-router-dom";
import moment from "moment";
import {startLikePost} from "../actions/posts";

export class Post extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      likes: props.likes,
      comments: props.comments
    };
  }

  onLike = () => {
    this.props.startLikePost(this.props.id);
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

        </div>
      </div>
    );
  }
}

const mapDispatchToProps = (dispatch) => ({
  startLikePost: (post_id) => dispatch(startLikePost(post_id))
});
    
export default connect(undefined, mapDispatchToProps)(Post);