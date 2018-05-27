import axios from 'axios';

axios.get('/api/users')
  .then((response) => {
    if (response.data.response === 'success') {
      console.log(response.data);
    }
  })
  .catch((e) => {
    console.log('error', e);
  });