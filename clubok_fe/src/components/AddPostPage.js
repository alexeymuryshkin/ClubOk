import React from 'react';
import {connect} from "react-redux";
import {startAddPost} from "../actions/posts";

class AddPostPage extends React.Component {
  state = {
    body: '',
    error: ''
  };

  onBodyChange = (e) => {
    const body = e.target.value.trim();
    this.setState(() => ({body}));
  };

  onSubmit = (e) => {
    e.preventDefault();

    if (!this.state.body) {
      this.setState(() => ({error: 'Body is empty'}))
    } else {
      const post = {
        body: this.state.body
      };

      this.props.startAddPost(post);
    }
  };

  render() {
    return (
      <div>
        <form onSubmit={this.onSubmit}>
          <textarea onChange={this.onBodyChange} placeholder="Body"/>
          <button>Ok</button>
        </form>
      </div>
    );
  };
}

const mapDispatchToProps = (dispatch) => ({
  startAddPost: (post) => dispatch(startAddPost(post))
});
    
export default connect(undefined, mapDispatchToProps)(AddPostPage);