import {applyMiddleware, combineReducers, compose, createStore} from 'redux';
import thunk from 'redux-thunk';
import authReducer from '../reducers/auth';
import clubsReducer from '../reducers/clubs';
import postsReducer from '../reducers/posts';

const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

export default () => {
  return createStore(
    combineReducers({
      auth: authReducer,
      clubs: clubsReducer,
      posts: postsReducer
    }),
    composeEnhancers(applyMiddleware(thunk))
  );
};