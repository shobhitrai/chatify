$(document).ready(function () {
   connect();
});

var webSocket;

function connect() {
   const protocol = location.protocol === 'https:' ? 'wss://' : 'ws://';
   const socketUrl = protocol + location.host + sessionPath + '/chat';
   console.log("Connecting to socket: " + socketUrl);

   try {
      webSocket = new WebSocket(socketUrl);
   } catch (e) {
      alert("WebSocket connection failed: " + e.message);
      window.location.href = sessionPath + '/login'
      return;
   }

   webSocket.onopen = () => {
      console.log("Connected to socket");
   };

   webSocket.onmessage = (event) => {
   let payload = null;
      try {
         payload = JSON.parse(event.data);
         console.log("From socket:", payload);
      } catch (e) {
         console.error("Invalid JSON from socket:", event.data);
      }
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

function globalSendToSocket(payload) {
   return new Promise((resolve, reject) => {
      try {
         if (!webSocket || webSocket.readyState !== WebSocket.OPEN) {
            return reject(new Error('WebSocket is not connected'));
         }

         webSocket.send(JSON.stringify(payload));
         // sending to socket succeeded
         resolve();
      } catch (error) {
         reject(error);
      }
   });
}

function incoming(payload) {
try {
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

      case "notification":
         appendNotification(payload);
         break;

      case "createChatGroup":
         createMainChat(payload);
         break;

      case "ackAcceptFriendRequest":
         ackAcceptFriendRequest(payload);
         break;

      case "addContact":
         addContact(payload);
         break;

      case "removeContact":
         removeContact(payload);
         break;

      case "ackGetChat":
         ackGetChat(payload);
         break;

      case "receivedTextMessage":
         receivedTextMessage(payload);
         break;

      case "onlineNotification":
         onlineNotification(payload);
         break;

      case "offlineNotification":
         offlineNotification(payload);
         break;

      default:
         console.warn("Unknown payload type:", payload.type);
   }
   } catch (error) {
      console.error("Error processing incoming payload:", error);
   }
}