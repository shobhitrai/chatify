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
   if (payload.status === 100) {
      const contact = payload.data;
      $('#accept-' + contactInfo.contactId).prop('disabled', false);
      addToContactList(contactInfo);
   } else {
      alert(payload.message);
   }
}

function addToContactList(contactInfo) {
    let data = '<a href="#" id="contact-'+contact.contactId+'" class="filterMembers all '
                +(contact.isOnline ? 'online' : 'offline')
                +' contact" data-toggle="list"><img class="avatar-md" src="'+contact.profileImage+'" '
                +'data-toggle="tooltip" data-placement="top" title="'+contact.firstName+'" alt="avatar">'
                +'<div class="status"><i class="material-icons online">fiber_manual_record</i></div>'
                +'<div class="data"><h5>'+contact.firstName+' '+contact.lastName+'</h5>'
                +'<p>Sofia, Bulgaria</p></div><div class="person-add"><i class="material-icons">person</i>												</div>											</a>';
    $('#contacts').prepend(data);
}