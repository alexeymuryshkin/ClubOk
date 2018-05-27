import configureMockStore from 'redux-mock-store';
import thunk from "redux-thunk";
import posts from '../fixtures/posts';
import {setPosts} from "../../actions/posts";


const createMockStore = configureMockStore([thunk]);

test('should set setPosts object with data', () => {
  const action = setPosts(posts);
  expect(action).toEqual({
    type: 'SET_POSTS',
    posts
  });
});