import React from 'react';
import LogInForm from "./LogInForm";

const AuthorizationPage = () => (
  <div>
    {/*<NavBar signedIn={false}/>*/}
    <h1>Login</h1>
    <LogInForm/>
    <h1>Signup</h1>

  </div>
);

export default AuthorizationPage;