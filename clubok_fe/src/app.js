import React from 'react';
import ReactDOM from 'react-dom';
import {Provider} from "react-redux";
import 'normalize.css/normalize.css';
import './styles/styles.scss';
import AppRouter from "./routers/AppRouter"
import configureStore from './store/configureStore';
import './playground/request';
import {startSetPosts} from './actions/posts';

const store = configureStore();
store.dispatch(startSetPosts());

const jsx = (
  <Provider store={store}>
    <AppRouter/>
  </Provider>
);

ReactDOM.render(jsx, document.getElementById('app'));