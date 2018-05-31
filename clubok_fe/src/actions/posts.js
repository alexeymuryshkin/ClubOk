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
      .then((response) => {
        console.log(response)
      })
      .catch((e) => {
        console.log(e);
        if (e.response) {
          console.log(e.response)
        }
      });
  };
};

export const likePost = (post_id) => ({
  type: 'LIKE_POST',
  post_id
});

export const startLikePost = (post_id) => {
  return (dispatch, getState) => {
    const token = getState().auth.token;
    axios.post(`/api/posts/${post_id}/likes`, {}, {
      headers: {
        'x-auth': token
      }
    })
      .then(() => {
        dispatch(likePost(post_id));
      })
      .catch((e) => {
        console.log(e);
        if (e.response) {
          console.log(e.response);
        }
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