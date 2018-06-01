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

export const likePost = (postId, userId) => ({
  type: 'LIKE_POST',
  postId,
  userId
});

export const unlikePost = (postId, userId) => ({
  type: 'UNLIKE_POST',
  postId,
  userId
});

export const startLikePost = (postId) => {
  return (dispatch, getState) => {
    const token = getState().auth.token;
    const userId = getState().auth.id;
    const post = getState().posts.filter((post) => post.id === postId)[0];

    if (post.likes.filter((id) => id === userId).length !== 0) {
      axios.delete(`/api/posts/${postId}/likes`, {
        headers: {
          'x-auth': token
        }
      })
        .then(() => {
          dispatch(unlikePost(postId, userId))
        })
        .catch((e) => {
          console.log(e);
          if (e.response) {
            console.log(e.response);
          }
        });
    } else {
      axios.post(`/api/posts/${postId}/likes`, {}, {
        headers: {
          'x-auth': token
        }
      })
        .then(() => {
          dispatch(likePost(postId, userId));
        })
        .catch((e) => {
          console.log(e);
          if (e.response) {
            console.log(e.response);
          }
        });
    }
  };
};

export const commentPost = (postId, comment) => ({
  type: 'COMMENT_POST',
  postId,
  comment
});

export const startCommentPost = (postId, comment) => {
  return (dispatch, getState) => {
    const token = getState().auth.token;
    axios.post(`/api/posts/${postId}/comments`, {...comment}, {
      headers: {
        'x-auth': token
      }
    })
      .then((response) => {
        dispatch(commentPost(postId, response.data.result));
      })
      .catch((e) => {
        console.log(e);
        if (e.response) {
          console.log(e.response);
        }
      });
  }
};

export const removeComment = (postId, commentId) => ({
  type: 'REMOVE_COMMENT',
  postId,
  commentId
});

export const startRemoveComment = (postId, commentId) => {
  return (dispatch, getState) => {
    const token = getState().auth.token;
    axios.delete(`/api/posts/${postId}/comments/${commentId}`, {
      headers: {
        'x-auth': token
      }
    })
      .then(() => {
        dispatch(removeComment(postId, commentId));
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