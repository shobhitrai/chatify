$(document).on('click', '.clear-history-btn', function () {
    if(!chatOpenIsChatAvailable)
        return;

    chatOpenIsChatAvailable = false;
    $('#chatgroup-' + chatOpenUserId).find('.data p').text('');
    populateChat(displayNoChatScreen(), chatOpenUserId, chatOpenProfileImage, chatOpenFirstName, chatOpenLastName, chatOpenIsOnline);
    sendPayloadToClearChat(chatOpenUserId, "clearChat");
});

$(document).on('click', '.block-contact-btn', function () {

});

function sendPayloadToClearChat(contactId, type) {
   const payload = {
      "contactId": chatOpenUserId
   }
   const socketReq = {
      "type": type,
      "payload": payload
   }
   webSocket.send(JSON.stringify(socketReq));
}