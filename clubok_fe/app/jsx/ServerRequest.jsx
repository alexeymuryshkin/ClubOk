// import jq from 'jquery';

class ServerRequest {
	
	sharedInstance = null;

	getInstance() {
	    if (this.sharedInstance == null)
            this.sharedInstance = new ServerRequest();
	    return this.sharedInstance;
    }

	constructor() {
		this.token = null;
	}

	// functions
	signUp(email, password, fname, lname) {
        console.log("New Sign Up request...");

        let attr = {"email": email,
			"password": password,
			"fname": fname,
			"lname": lname
		};

        var serv_request = new XMLHttpRequest();
        serv_request.onreadystatechange = function() {
            if (serv_request.readyState == 4) {
                if (serv_request.status == 201) {
                    console.log("Success!");
                    console.log(serv_request.responseText);
                    //document.getElementById(id).value = '';

                    jo = JSON.parse(serv_request.responseText);
                    this.onSignUpResponse(jo);
                } else if (serv_request.status == 400) {
					console.log("Sign Up Failed!(")
				}
        	}
        }
        serv_request.open('POST', 'api', true);
        //serv_request.setRequestHeader("content-type", "application/x-www-form-urlencoded");
        serv_request.send(attrs);
	}

	onSignUpResponse(jo) {
		this.token = jo.tokens[0].token;
	}

	signIn(email, password) {
        console.log("New Sign In request...");

        let attr = {"email": email,
            "password": password
        };

        var serv_request = new XMLHttpRequest();
        serv_request.onreadystatechange = function() {
            if (serv_request.readyState == 4 && serv_request.status == 200) {
                console.log("Success!");
                console.log(serv_request.responseText);
                //document.getElementById(id).value = '';

                jo = JSON.parse(serv_request.responseText);
                this.onSignInResponse(jo);
            }
        }
        serv_request.open('POST', 'api', true);
        //serv_request.setRequestHeader("content-type", "application/x-www-form-urlencoded");
        serv_request.send(attrs);
	}

	onSignInResponse(jo) {
        this.token = jo.tokens[0].token;
	}

    signOut() {
        console.log("Sign Out request...");

        var serv_request = new XMLHttpRequest();
        serv_request.onreadystatechange = function() {
            if (serv_request.readyState == 4 && serv_request.status == 204) {
                console.log("Success!");
                this.onSignOutSuccess();
            }
        }
        serv_request.open('DELETE', 'api', true);
        serv_request.setRequestHeader("x-auth", this.token);
        serv_request.send(attrs);
    }

    onSignOutSuccess() {
		this.token = null;
	}
}

export default ServerRequest;