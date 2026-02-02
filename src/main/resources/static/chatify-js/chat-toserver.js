$(document).on('click', '.filterDiscussions', function() {
   chatClicked(this);
});

function chatClicked(element) {
   const contactId = $(element).attr('id').replace('chatgroup-', '');
   chatWindowOpenUserId = contactId;
   let color = $('#p-'+contactId).css('color');
   if(color === 'rgb(33, 37, 41)') {
      $('#p-'+contactId).attr('style', 'color: #bdbac2;');
   }
   const payload = {
      "contactId": contactId
   }
   const socketReq = {
      "type": "getChat",
      "payload": payload
   }
   webSocket.send(JSON.stringify(socketReq));

}