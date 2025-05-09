$('.fr-accept-btn').on('click', function(){
	acceptFriendRequest(this);
});


function createChatGroup(payload) {
   if (payload.status === 100) {
      let chatGroup = payload.data.chatGroups;
      let data = '<a id="user-'+chatGroup.senderId+'" ' +
         'href="#chat-' + chatGroup.senderId + '" class="filterDiscussions all unread single" ' +
         'data-toggle="list"><img class="avatar-md" src="' + chatGroup.senderProfileImage + '" ' +
         'data-toggle="tooltip" data-placement="top" title="'+chatGroup.senderFirstName+'" ' +
         'alt="avatar"><div class="status"><i class="material-icons offline">' +
         'fiber_manual_record</i></div><div class="new bg-gray"><span>?</span></div>' +
         '<div class="data"><h5>' + chatGroup.senderFirstName + ' ' + chatGroup.senderLastName + '</h5>' +
         '<span>' + chatGroup.chats[0].formattedDate + '</span><p>' + chatGroup.chats[0].message + '</p></div></a>';
      $('#chats').prepend(data);
   }
}

function acceptFriendRequest(element) {
   let senderId = $(element).attr('id').replace('accept-', '');
   $(element).prop('disabled', true);
   const payload = {
      "senderId": senderId
   }
   const socketReq = {
      "type" : "acceptFriendRequest",
      "payload": payload
   }
   webSocket.send(JSON.stringify(socketReq));
}

function ackAcceptFriendRequest(payload) {

if(payload.status === 100) {
    const contactInfo = payload.data;
    $('#accept-' + contactInfo.contactId).prop('disabled', false);
    }
    else {
        alert(payload.message);
    }
}