const postsReducerDefaultState = [];

export default (state = postsReducerDefaultState, action) => {
  switch (action.type) {
    case 'CREATE_POST':
      return [
        ...state,
        action.post
      ];
    case 'SET_POSTS':
      return action.posts;
    default:
      return state;
  }
};