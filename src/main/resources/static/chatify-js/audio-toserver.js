$(document).on('click', '.audio', function () {
    sendPayloadForAudio(chatOpenUserId, "callRequest");
});

$(document).on("click", ".btn.back", function () {
    const userId = $(this).closest(".babble").attr("id").split("-")[1];
    $("#chat-" + userId + " .call").hide();
    $("#chat-" + userId + " .chat").show();
});

function sendPayloadForAudio(contactId, type) {
   const payload = {
      "contactId": contactId
   }
   const socketReq = {
      "type": type,
      "payload": payload
   }
   webSocket.send(JSON.stringify(socketReq));
}