const postsReducerDefaultState = [];

export default (state = postsReducerDefaultState, action) => {
  switch (action.type) {
    case 'CREATE_POST':
      return [
        ...state,
        action.post
      ];
    case 'LIKE_POST':
      return state;
    case 'SET_POSTS':
      return action.posts;
    default:
      return state;
  }
};