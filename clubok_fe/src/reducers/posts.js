const postsReducerDefaultState = [{
  id: '1',
  body: 'Some text',
  
}];

export default (state = postsReducerDefaultState, action) => {
  switch (action.type) {
    case 'ADD_POST':
      return [
        ...state,
        action.post
      ];
    default:
      return state;
  }
};