import React from 'react';
import {connect} from "react-redux";
import axios from 'axios';
import {startAddPost} from "../actions/posts";

class AddPostPage extends React.Component {
  state = {
    clubId: '',
    body: '',
    clubs: [],
    error: ''
  };

  componentDidMount() {
    axios.get('/api/clubs', {
      headers: {
        'x-auth': this.props.token
      }
    })
      .then((response) => {
        // console.log(response);
        this.setState(() => ({clubs: response.data.result}));
      })
      .catch((e) => {
        console.log(e);
      });
  }

  onClubChange = (e) => {
    const clubId = e.target.value;
    this.setState(() => ({clubId}));
  };

  onBodyChange = (e) => {
    const body = e.target.value.trim();
    this.setState(() => ({body}));
  };

  onSubmit = (e) => {
    e.preventDefault();

    if (!this.state.body || !this.state.clubId) {
      this.setState(() => ({error: 'Body is empty'}))
    } else {
      const post = {
        clubId: this.state.clubId,
        body: this.state.body
      };

      this.props.startAddPost(post);
    }
  };

  render() {
    return (
      <div>
        <form onSubmit={this.onSubmit}>
          <select
            value={this.state.clubId}
            onChange={this.onClubChange}
          >
            <option disabled value="">Select Club</option>
            {this.state.clubs.map((club) => <option key={club.id} value={club.id}>{club.name}</option>)}
          </select>
          <textarea onChange={this.onBodyChange} placeholder="Body"/>
          <button>Ok</button>
        </form>
      </div>
    );
  };
}

const mapStateToProps = (state) => ({
  token: state.auth.token
});

const mapDispatchToProps = (dispatch) => ({
  startAddPost: (post) => dispatch(startAddPost(post))
});
    
export default connect(mapStateToProps, mapDispatchToProps)(AddPostPage);