import React from 'react';
import {Link} from "react-router-dom";

class NavBar extends React.Component {
  render() {
    return (
      <header className="navbar">
        <div className="navbar__left">
          <Link to="/" className="navbar__brand">
            <img src="images/logo.png" alt="ClubOk"/>
          </Link>
          <span className="navbar__divider"/>
          <div className="navbar__search">
            <input type="text" placeholder="Search..." />
              <span className="icon-search"/>
          </div>
        </div>
        <div className="navbar__right">
          <div className="navbar__profile" href="">
            <div className="avatar-container round xs">
              <img src="images/avatar.jpg" alt="" />
            </div>
            <p>User</p>
            <ul className="navbar__profile-items">
              <li className="navbar__profile-item">Profile</li>
              <li className="navbar__profile-item">Settings</li>
              <li className="navbar__profile-item--danger">Logout</li>
            </ul>
          </div>
        </div>
      </header>
    );
  }
}

export default NavBar;