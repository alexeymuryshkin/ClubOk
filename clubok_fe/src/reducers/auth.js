const authReducerDefaultState = {
  token: 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhY2Nlc3MiOiJhdXRoIiwiaWQiOiI1YjBkYTU4MDJhOTZhNjI3MmM2YjUzOTAiLCJpYXQiOjE1Mjc2MjA5OTJ9.C_28RHzIn9xfh1Eqz5_zz0BSWq6uZKDA2s7XsfLw_jA'
};

export default (state = authReducerDefaultState, action) => {
  switch (action.type) {
    case 'LOGIN':
      return {token: action.token};
    default:
      return state;
  }
};