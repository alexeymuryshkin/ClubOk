import React from 'react';
import {connect} from 'react-redux';
import validator from 'validator';
import {addUser} from "../actions/users";

export class AddUserPage extends React.Component {
  state = {
    email: '',
    password: '',
    fname: '',
    lname: '',
    permissionLevel: '1',
    error: ''
  };

  onEmailChange = (e) => {
    const email = e.target.value;
    this.setState(() => ({email}));
  };

  onPasswordChange = (e) => {
    const password = e.target.value;
    this.setState(() => ({password}));
  };

  onFirstNameChange = (e) => {
    const fname = e.target.value;
    this.setState(() => ({fname}));
  };

  onLastNameChange = (e) => {
    const lname = e.target.value;
    this.setState(() => ({lname}));
  };

  onPermissionLevelChange = (e) => {
    const permissionLevel = e.target.value;
    this.setState(() => ({permissionLevel}));
  };

  onSubmit = (e) => {
    e.preventDefault();

    if (!this.state.email || !this.state.password) {
      this.setState(() => ({error: 'You should provide email and password'}));
    } else if (!validator.isEmail(this.state.email)) {
      this.setState(() => ({error: 'Email is invalid'}));
    } else {
      this.setState(() => ({error: ''}));
      const user = {
        email: this.state.email,
        password: this.state.password,
        fname: this.state.fname,
        lname: this.state.lname,
        permissionLevel: this.state.permissionLevel
      };

      this.props.addUser(user);
    }
  };

  render() {
    return (
      <div>
        <form onSubmit={this.onSubmit}>
          <input
            type="email"
            placeholder="Email"
            onChange={this.onEmailChange}
          />
          <input
            type="password"
            placeholder="Password"
            onChange={this.onPasswordChange}
          />
          <input
            type="text"
            placeholder="First Name"
            onChange={this.onFirstNameChange}
          />
          <input
            type="text"
            placeholder="Last Name"
            onChange={this.onLastNameChange}
          />
          <select value={this.state.permissionLevel} onChange={this.onPermissionLevelChange}>
            <option value="1">Regular User</option>
            <option value="5">Administrator</option>
          </select>
          <button>Save</button>
        </form>
      </div>
    );
  };
}

const mapDispatchToProps = (dispatch) => ({
  addUser: (user) => dispatch(addUser(user))
});
    
export default connect(undefined, mapDispatchToProps)(AddUserPage);