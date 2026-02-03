$(document).on('click', '.filterDiscussions', function(e) {
   e.preventDefault();
   chatClicked(this);
});

$(document).on('click', '.filterMembers', function(e) {
   e.preventDefault();
   openChat(this);
});

function chatClicked(element) {
   const contactId = $(element).attr('id').replace('chatgroup-', '');
   chatWindowOpenUserId = contactId;
   let color = $('#p-' + contactId).css('color');
   if (color === 'rgb(33, 37, 41)') {
      $('#p-' + contactId).attr('style', 'color: #bdbac2;');
   }
   sendPayloadToGetChat(contactId);
}

function openChat(element) {
   const contactId = $(element).attr('id').replace('contact-', '');
   chatWindowOpenUserId = contactId;
   sendPayloadToGetChat(contactId);
}

function sendPayloadToGetChat(contactId) {
   const payload = {
      "contactId": contactId
   }
   const socketReq = {
      "type": "getChat",
      "payload": payload
   }
   webSocket.send(JSON.stringify(socketReq));
}