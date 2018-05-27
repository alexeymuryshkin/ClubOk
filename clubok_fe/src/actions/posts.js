import axios from 'axios';

export const addPost = (post) => ({
  type: 'CREATE_POST',
  post
});

export const startAddPost = (post) => {
  return (dispatch, getState) => {
    const token = getState().auth.token;
    axios.post('/api/posts', {
      ...post
    }, {
      headers: {
        'x-auth': token
      }
    })
      .then(() => {
        dispatch(addPost(post));
      })
      .catch((e) => {
        console.log(e);
      });
  };
};

export const setPosts = (posts) => ({
  type: 'SET_POSTS',
  posts
});

export const startSetPosts = () => {
  return (dispatch, getState) => {
    const token = getState().auth.token;
    axios.get('/api/posts', {
      headers: {
        'x-auth': token
      }
    })
      .then((response) => {
        dispatch(setPosts(response.data.result));
      })
      .catch((e) => {
        console.log(e);
      });
  };
};