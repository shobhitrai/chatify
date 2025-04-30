$(document).ready(function() {
	connect();
});

const webSocket;

function connect() {
    const userId = sessionStorage.getItem('sessionUserId');
    const socketUrl = 'ws://${document.location.host}/chatify/socket/${userId}';
    webSocket = new WebSocket(socketUrl);

    webSocket.onmessage = handleIncomingMessage;
    webSocket.onerror = handleSocketError;
}

function handleIncomingMessage(event) {
    try {
        const payload = JSON.parse(event.data);
        console.log("From socket: " + JSON.stringify(payload));
        incomming(payload);
    } catch (error) {
        console.error("Error parsing incoming message: ", error);
    }
}

function handleSocketError(error) {
    console.error("WebSocket error: ", error);
}

function incomming(payload) {
	switch (payload.type) {
	case "notificationCount":
		socNotificationCount();
		break;
	}
}