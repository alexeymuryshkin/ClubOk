import User from './User';

class Session {
	static sharedInstance = this.sharedInstance == null ? new Session() : this.sharedInstance;

	token = null;
	curUser = null;

	getToken() {
		return this.token;
	}

	setToken(token) {
		this.token = token;
	}

	getUser() {
		return this.curUser;
	}

	setUser(user) {
		this.curUser = user;
	}
}

export default Session;