import {applyMiddleware, combineReducers, compose, createStore} from 'redux';

const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

export default () => {
  return createStore(
    combineReducers({}),
    composeEnhancers(applyMiddleware())
  );
};