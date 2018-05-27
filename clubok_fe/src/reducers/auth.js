const authReducerDefaultState = {
  token: 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhY2Nlc3MiOiJhdXRoIiwiaWQiOiI1YWViMmY2NjM5ZmEzNTE5NmM5YTY3MTMiLCJpYXQiOjE1MjUzNjI1MzR9.BgLjRe5IJDp0TDN18kZwcQIpZn1museBEvMOiYM-Y3U'
};

export default (state = authReducerDefaultState, action) => {
  switch (action.type) {
    case 'LOGIN':
      return {token: action.token};
    default:
      return state;
  }
};