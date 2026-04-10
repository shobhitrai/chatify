function ackCallRequest(payload) {
    if(payload.status !== 100) {
        alert(payload.message);
    }
}

function callAcceptDeny(payload) {
    const contactId = payload.data.userId;
    $("#chat-" + contactId + " .chat").hide();
    $("#chat-" + contactId + " .call").show();
}