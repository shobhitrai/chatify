$(document).on('click', '.clear-history-btn', function () {
    if(!chatOpenIsChatAvailable)
        return;

    chatOpenIsChatAvailable = false;
    populateChat(displayNoChatScreen(), chatOpenUserId, chatOpenProfileImage, chatOpenFirstName, chatOpenLastName, chatOpenIsOnline);
    sendPayloadToClearChat(chatOpenUserId);
});

function sendPayloadToClearChat(contactId) {
   const payload = {
      "contactId": chatOpenUserId
   }
   const socketReq = {
      "type": "clearChat",
      "payload": payload
   }
   webSocket.send(JSON.stringify(socketReq));
}