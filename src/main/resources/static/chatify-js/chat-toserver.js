let chatOpenContactId;

$(document).on('click', '.filterDiscussions', function() {
   chatClicked(this);
});

function chatClicked(element) {
    const contactId = $(element).attr('id').replace('chatgroup-', '');
    let color = $('#p-'+contactId).css('color');
    console.log('color: ', color);
    if(color === 'rgb(33, 37, 41)') {
    $('#p-'+contactId).attr('style', 'color: #bdbac2;');
    chatOpenContactId = contactId;
    const payload = {
              "contactId": contactId
           }
           const socketReq = {
              "type" : "seenLastMsg",
              "payload": payload
           }
        webSocket.send(JSON.stringify(socketReq));
        }
}