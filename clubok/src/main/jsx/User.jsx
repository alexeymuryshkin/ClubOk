
class User {
	id = null;
	name = null;
    subscriptions = null;

    constructor(id, name) {
    	this.id = id;
    	this.name = name;
    }

    getSubscriptions() {
    	return this.subscriptions;
    }

    setSubscriptions() {
    	this.subscriptions = subscriptions;
    }
}

export default User;