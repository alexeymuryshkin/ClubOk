import React from 'react';
import {connect} from 'react-redux';
import validator from 'validator';
import {startLogin} from "../actions/auth";

class LogInForm extends React.Component {
  state = {
    email: '',
    password: '',
    error: ''
  };

  onEmailChange = (e) => {
    const email = e.target.value.trim();
    this.setState(() => ({email}));
  };

  onPasswordChange = (e) => {
    const password = e.target.value.trim();
    this.setState(() => ({password}));
  };

  onSubmit = (e) => {
    e.preventDefault();

    if (!this.state.email || !this.state.password) {
      this.setState(() => ({error: 'Please enter email and password'}));
    } else if (!validator.isEmail(this.state.email)) {
      this.setState(() => ({error: 'Email is not properly formatted'}));
    } else {
      this.setState(() => ({error: ''}));
      this.props.startLogin(this.state.email, this.state.password)
        .catch((e) => {
          if (e.response) {
            this.setState(() => ({error: e.response.data.details}));
          }
        });
    }
  };

  render() {
    return (
      <div>
        <form onSubmit={this.onSubmit}>
          {this.state.error && <p>{this.state.error}</p> }
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
          <button>Log In</button>
        </form>
      </div>
    );
  };
}


const mapDispatchToProps = (dispatch) => ({
  startLogin: (email, password) => dispatch(startLogin(email, password))
});

export default connect(undefined, mapDispatchToProps)(LogInForm);