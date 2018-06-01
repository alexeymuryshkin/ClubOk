const postsReducerDefaultState = [];

export default (state = postsReducerDefaultState, action) => {
  switch (action.type) {
    case 'CREATE_POST':
      return [
        ...state,
        action.post
      ];
    case 'LIKE_POST':
      return state.map((post) => {
        if (post.id === action.postId) {
          return {
            ...post,
            likes: [
              ...post.likes,
              action.userId
            ]
          };
        } else {
          return post;
        }
      });
    case 'UNLIKE_POST':
      return state.map((post) => {
        if (post.id === action.postId) {
          return {
            ...post,
            likes: post.likes.filter((id) => id !== action.userId)
          };
        } else {
          return post;
        }
      });
    case 'COMMENT_POST':
      return state.map((post) => {
        if (post.id === action.postId) {
          return {
            ...post,
            comments: [
              ...post.comments,
              action.comment
            ]
          };
        } else {
          return post;
        }
      });
    case 'REMOVE_COMMENT':
      return state.map((post) => {
        if (post.id === action.postId) {
          return {
            ...post,
            comments: post.comments.filter((comment) => comment.id !== action.commentId)
          };
        } else {
          return post;
        }
      });
    case 'SET_POSTS':
      return action.posts;
    default:
      return state;
  }
};