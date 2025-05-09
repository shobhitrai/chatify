$(document).ready(function () {
   connect();
});

var webSocket;

function connect() {
   const socketUrl = 'ws://' + document.location.host + sessionPath + '/chat';
   console.log("Connecting to socket: " + socketUrl);
   webSocket = new WebSocket(socketUrl);

   webSocket.onopen = () => {
      console.log("Connected to socket");
   };

   webSocket.onmessage = (event) => {
      let payload = JSON.parse(event.data);
      console.log("From socket" + JSON.stringify(payload));
      incoming(payload);
   };

   webSocket.onclose = (event) => {
      if (event.wasClean) {
         console.log('Connection closed cleanly, code=${event.code} reason=${event.reason}');
      } else {
         alert('Connection died, please login again.');
         window.location.href = sessionPath + '/login'
      }
   };

   webSocket.onerror = (error) => {
      console.error("WebSocket error:", error);
   };
}

function incoming(payload) {
   switch (payload.type) {
      case "invalidSession":
         alert("Session expired, please login again.");
         window.location.href = sessionPath + '/login';
         break;
      case "ackFriendRequest":
         ackFriendRequest(payload);
         break;
      case "ackSearchedUsers":
         ackSearchedUsers(payload);
         break;
      case "createChatGroup":
         createMainChat(payload);
         createChatGroup(payload);
         appendNotification(payload);
         break;
      case "appendChatGroup":


         break;
   }
}