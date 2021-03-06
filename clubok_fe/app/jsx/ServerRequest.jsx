// import jq from 'jquery';

class ServerRequest {
	
	static sharedInstance = null;

	static getInstance() {
	    if (this.sharedInstance == null)
            this.sharedInstance = new ServerRequest();
	    return this.sharedInstance;
    }

	constructor() {

	}

	// functions
	signUp(data) {
        console.log("New Sign Up request...");

        let attrs = {email: data.email,
			password: data.password,
			fname: data.fname,
			lname: data.lname
		};

        let myClass = this;

        let serv_request = new XMLHttpRequest();
        serv_request.onreadystatechange = function() {
            if (serv_request.readyState == 4) {
                if (serv_request.status == 201) {
                    console.log("Success!");
                    console.log(serv_request.responseText);
                    //document.getElementById(id).value = '';

                    //let jo = JSON.parse(serv_request.responseText);
                    let jo = serv_request.getResponseHeader("x-auth");
                    myClass.onSignUpResponse(jo);
                } else if (serv_request.status == 400) {
					console.log("Sign Up Failed!(")
				}
        	}
        };
        serv_request.open('POST', 'api/users', true);
        //serv_request.setRequestHeader("content-type", "application/x-www-form-urlencoded");
        serv_request.send(JSON.stringify(attrs));
	}

	onSignUpResponse(jo) {
        window.sessionStorage.setItem('token', jo);
        window.location.replace("http://localhost:3000/feed.html");
	}

	signIn(data) {
        console.log("New Sign In request...");

        let attrs = {
            email: data.email,
            password: data.password
        };

        let myClass = this;

        let serv_request = new XMLHttpRequest();
        serv_request.onreadystatechange = function() {
            if (serv_request.readyState == 4 && serv_request.status == 200) {
                console.log("Success!");
                console.log(serv_request.responseText);
                //document.getElementById(id).value = '';

                //let jo = JSON.parse(serv_request.responseText);
                let jo = serv_request.getResponseHeader("x-auth");
                myClass.onSignInResponse(jo);
            }
        };
        serv_request.open('POST', 'api/users/login', true);
        //serv_request.setRequestHeader("content-type", "application/x-www-form-urlencoded");
        serv_request.send(JSON.stringify(attrs));
	}

	onSignInResponse(jo) {
        window.sessionStorage.setItem('token', jo);
        window.location.replace("http://localhost:3000/feed.html");
	}

    signOut() {
        console.log("Sign Out request...");

        let myClass = this;

        let serv_request = new XMLHttpRequest();
        serv_request.onreadystatechange = function() {
            if (serv_request.readyState == 4 && serv_request.status == 204) {
                console.log("Success!");
                myClass.onSignOutSuccess();
            }
        };
        serv_request.open('DELETE', 'api/users/me/token', true);
        serv_request.setRequestHeader("x-auth", window.sessionStorage.getItem('token'));
        serv_request.send();
    }

    onSignOutSuccess() {
        window.sessionStorage.removeItem('token');
        window.location.replace("http://localhost:3000");
	}

	getUsers(data) {
        console.log("New Get Users request...");

        // let attrs = {
        //     size: data.size,
        //     page: data.page,
        //     orderBy: data.orderBy,
        //     order: data.order
        // };
        let attrs = '';
        if (data.size) attrs += (attrs ? '&' : '') + 'size=' + data.size;
        if (data.page) attrs += (attrs ? '&' : '') + 'page=' + data.page;
        if (data.orderBy) attrs += (attrs ? '&' : '') + 'orderBy=' + data.orderBy;
        if (data.order) attrs += (attrs ? '&' : '') + 'order=' + data.order;

        let myClass = this;

        let serv_request = new XMLHttpRequest();
        serv_request.onreadystatechange = function() {
            if (serv_request.readyState == 4 && serv_request.status == 200) {
                console.log("Success!");
                console.log(serv_request.responseText);
                //document.getElementById(id).value = '';

                let jo = JSON.parse(serv_request.responseText);
                //let jo = serv_request.getResponseHeader("x-auth");
                myClass.onGetUsersResponse(jo);
            }
        };
        serv_request.open('GET', 'api/users' + (attrs ? '?' : '') + attrs, true);
        //serv_request.setRequestHeader("x-auth", window.sessionStorage.getItem('token'));
        serv_request.send(null);
    }

    onGetUsersResponse(jo) {

    }

    getUsersMe() {
        console.log("New Get UsersMe request...");

        let attrs = {};

        let myClass = this;

        let serv_request = new XMLHttpRequest();
        serv_request.onreadystatechange = function() {
            if (serv_request.readyState == 4 && serv_request.status == 200) {
                console.log("Success!");
                console.log(serv_request.responseText);
                //document.getElementById(id).value = '';

                let jo = JSON.parse(serv_request.responseText);
                //let jo = serv_request.getResponseHeader("x-auth");
                myClass.onGetUsersMeResponse(jo);
            }
        };
        serv_request.open('GET', 'api/users/me', true);
        serv_request.setRequestHeader("x-auth", window.sessionStorage.getItem('token'));
        serv_request.send(null);
    }

    onGetUsersMeResponse(jo) {

    }

    getPosts(data) {
        console.log("New Get Posts request...");

        // let attrs = {
        //     size: data.size,
        //     page: data.page,
        //     orderBy: data.orderBy,
        //     order: data.order
        // };

        let attrs = '';
        if (data.size) attrs += (attrs ? '&' : '') + 'size=' + data.size;
        if (data.page) attrs += (attrs ? '&' : '') + 'page=' + data.page;
        if (data.orderBy) attrs += (attrs ? '&' : '') + 'orderBy=' + data.orderBy;
        if (data.order) attrs += (attrs ? '&' : '') + 'order=' + data.order;

        let myClass = this;

        let serv_request = new XMLHttpRequest();
        serv_request.onreadystatechange = function() {
            if (serv_request.readyState == 4 && serv_request.status == 200) {
                console.log("Success!");
                console.log(serv_request.responseText);
                //document.getElementById(id).value = '';

                let jo = JSON.parse(serv_request.responseText);
                //let jo = serv_request.getResponseHeader("x-auth");
                myClass.onGetPostsResponse(jo);
            }
        };
        serv_request.open('GET', 'api/posts' + (attrs ? '?' : '') + attrs, true);
        //serv_request.setRequestHeader("x-auth", window.sessionStorage.getItem('token'));
        serv_request.send(null);
    }

    onGetPostsResponse(jo) {

    }

    postPosts(data) {
        console.log("New Post Posts request...");

        let attrs = {
            clubId: data.clubId,
            userId: data.userId,
            postedAt: data.postedAt,
            type: data.type,
            body: data.body,
            images: data.images,
            event: data.event,
            likes: data.likes,
            comments: data.comments
        };

        let myClass = this;

        let serv_request = new XMLHttpRequest();
        serv_request.onreadystatechange = function() {
            if (serv_request.readyState == 4 && serv_request.status == 200) {
                console.log("Success!");
                console.log(serv_request.responseText);
                //document.getElementById(id).value = '';

                let jo = serv_request.responseText;
                //let jo = JSON.parse(serv_request.responseText);
                //let jo = serv_request.getResponseHeader("x-auth");
                myClass.onPostPostsResponse(jo);
            }
        };
        serv_request.open('POST', 'api/posts', true);
        serv_request.setRequestHeader("x-auth", window.sessionStorage.getItem('token'));
        serv_request.send(JSON.stringify(attrs));
    }

    onPostPostsResponse(jo) {

    }

    getPostsId(data) {
        console.log("New Get Posts Id request...");

        let attrs = {};

        let myClass = this;

        let serv_request = new XMLHttpRequest();
        serv_request.onreadystatechange = function() {
            if (serv_request.readyState == 4 && serv_request.status == 200) {
                console.log("Success!");
                console.log(serv_request.responseText);
                //document.getElementById(id).value = '';

                let jo = serv_request.responseText;
                //let jo = JSON.parse(serv_request.responseText);
                //let jo = serv_request.getResponseHeader("x-auth");
                myClass.onGetPostIdResponse(jo);
            }
        };
        serv_request.open('GET', 'api/posts/' + data.postId, true);
        //serv_request.setRequestHeader("x-auth", window.sessionStorage.getItem('token'));
        serv_request.send(null);
    }

    onGetPostIdResponse(jo) {

    }
}

export default ServerRequest;