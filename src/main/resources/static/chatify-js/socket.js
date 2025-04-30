$(document).ready(function() {
	connect();
});

var webSocket;

function connect() {
    const userId = sessionStorage.getItem('sessionUserId');
    const socketUrl = 'ws://' + document.location.host + '/chatify/chat/' + userId;
    console.log("Connecting to socket: " + socketUrl);
    webSocket = new WebSocket(socketUrl);

    webSocket.onopen = () => {
      console.log("Connected to WebSocket server");
    };

    webSocket.onmessage = (event) => {
      console.log("Received message:", event.data);
    };

    webSocket.onclose = (event) => {
      if (event.wasClean) {
        console.log('Connection closed cleanly, code=${event.code} reason=${event.reason}');
      } else {
        console.error('Connection died');
      }
    };

    webSocket.onerror = (error) => {
      console.error("WebSocket error:", error);
    };
}

function incomming(payload) {
	switch (payload.type) {
	case "notificationCount":
		socNotificationCount();
		break;
	}
}