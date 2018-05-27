import axios from 'axios';

export const login = (token) => ({
  type: 'LOGIN',
  token
});

export const startLogin = (email, password) => {
  return (dispatch) => {
    return axios.post('/api/users/login', {email, password})
      .then((response) => {
        dispatch(login(response.headers['x-auth']));
      })
      .catch((e) => {
        console.log(e);
      });
  };
};