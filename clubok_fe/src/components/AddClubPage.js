import React from 'react';
import {connect} from "react-redux";
import {addClub} from "../actions/clubs";

class AddClubPage extends React.Component {
  state = {
    name: '',
    description: '',
    error: ''
  };

  onNameChange = (e) => {
    const name = e.target.value.trim();
    this.setState(() => ({name}));
  };

  onSubmit = (e) => {
    e.preventDefault();

    if (!this.state.name) {
      this.setState(() => ({error: 'Club name must be specified'}));
    } else {
      this.setState(() => ({error: ''}));

      const club = {
        name: this.state.name,
        description: this.state.description
      };

      this.props.addClub(club);
    }
  };

  render() {
    return (
      <div>
        <form onSubmit={this.onSubmit}>
          {this.state.error && <p>{this.state.error}</p>}
          <input
            type="text"
            placeholder="Name"
            onChange={this.onNameChange}
          />
          <button>Save</button>
        </form>
      </div>
    );
  };
}

const mapDispatchToProps = (dispatch) => ({
  addClub: (club) => dispatch(addClub(club))
});
    
export default connect(undefined, mapDispatchToProps)(AddClubPage);