// import jq from 'jquery';

class WebSocketDemo {

    constructor(updateFeed) {
        this.updateFeed = updateFeed;

        this.webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");
        this.webSocket.onmessage = function (msg) {
            this.updateFeed(msg);
        };
        this.webSocket.onclose = function () {
            alert("WebSocket connection closed")
        };
    }
}

export default WebSocketDemo;