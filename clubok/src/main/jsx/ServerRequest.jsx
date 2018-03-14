import jq from 'jquery';

class ServerRequest {
	
	static sharedInstance = this.sharedInstance == null ? new ServerRequest() : this.sharedInstance;

	doGetRequest(params, applyChanges) {
		console.log("New GET request...");
		var serv_request = new XMLHttpRequest();
		serv_request.onreadystatechange = function() {
			if (serv_request.readyState == 4 && serv_request.status == 200) {
				console.log("Success!");
						
				jo = JSON.parse(serv_request.responseText);
				applyChanges(jo);
			}
		}
		serv_request.open('GET', 'clubok/api', true);
		serv_request.send(null);
	}

	doPostRequest(params, applyChanges) {
		text = document.getElementById(id).value;
		console.log("New POST request...");
				
		var serv_request = new XMLHttpRequest();
		serv_request.onreadystatechange = function() {
			if (serv_request.readyState == 4 && serv_request.status == 200) {
				console.log("Success!");
				console.log(serv_request.responseText);
				document.getElementById(id).value = '';
						
				jo = JSON.parse(serv_request.responseText);
				applyChanges(jo);
			}
		}
		serv_request.open('POST', 'clubok/api', true);
		serv_request.setRequestHeader("content-type", "application/x-www-form-urlencoded");
		serv_request.send(params);
	}

	doDeleteRequest(params, applyChanges) {
		console.log("New DELETE request...");
		var serv_request = new XMLHttpRequest();
		serv_request.onreadystatechange = function() {
			if (serv_request.readyState == 4 && serv_request.status == 200) {
				console.log("Success!");
				//console.log(serv_request.responseText);
						
				jo = JSON.parse(serv_request.responseText);
				applyChanges(jo);
			}
		}
		serv_request.open('DELETE', 'clubok/api', true);
		serv_request.send(null);
	}

	signIn(email, password) {
	
	}

	signUp(email, password) {

	}

	signOut() {

	}

	follow(clubId) {
	
	}

	getPosts() {

	}

	getPosts(clubId) {

	}
}

export default ServerRequest;