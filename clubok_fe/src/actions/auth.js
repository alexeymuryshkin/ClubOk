import axios from 'axios';

export const login = (token, id) => ({
  type: 'LOGIN',
  token,
  id
});

export const startLogin = (email, password) => {
  return (dispatch) => {
    return axios.post('/api/users/login', {email, password})
      .then((response) => {
        dispatch(login(response.headers['x-auth'], response.data.userId));
      })
      .catch((e) => {
        console.log(e);
      });
  };
};