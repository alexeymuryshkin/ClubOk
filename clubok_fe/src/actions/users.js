import axios from 'axios';

export const addUser = (user) => {
  return () => {
    return axios.post('/api/users', {
      ...user
    })
      .then((response) => {
        console.log(response.data.user_id);
      })
      .catch((e) => {
        console.log(e);
      });
  };
};