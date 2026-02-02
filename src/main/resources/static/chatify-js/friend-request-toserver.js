let typingTimer = null; // Timer identifier
const typingDelay = 200;
let acceptedSenderId = null;
let rejectedSenderId = null;

$('#user').on('keyup', function (e) {
     clearTimeout(typingTimer); // Clear the previous timer
     const element = this;
     typingTimer = setTimeout(function () {
        getSearchedUsers(e, element); // Call the function after the delay
     }, typingDelay);
});

function getSearchedUsers(e, element) {
   if ((e.which >= 48 && e.which <= 57) ||
      (e.which >= 65 && e.which <= 90) ||
      (e.which >= 97 && e.which <= 122) ||
      e.which == 8 || e.which == 46) {
      let username = $(element).val().trim();
      if (username !== '') {
          const payload = {
            "username": username
          }
          const socketReq = {
            "type" : "searchedUsers",
            "payload": payload
          }
        webSocket.send(JSON.stringify(socketReq));
      } else {
            $('#searched-user').html('');
      }
   }
}

$('#searched-user').on('click', '.searched-user-btn', function () {
    getSearchedIcon(this);
});

function getSearchedIcon(element) {
   friendFirstName = $(element).data('firstname');
   friendLastName = $(element).data('lastname');
   friendUserId = $(element).data('userid');
   friendName = friendFirstName + ' ' + friendLastName;
   friendProfileImage = $(element).data('profileimage');
   $('#searched-user').html('');
   var data = '<div class="user" id="contact">' +
      '<img class="avatar-sm" src="' + friendProfileImage + '" alt="avatar">' +
      '<h5>' + friendFirstName + ' ' + friendLastName + '</h5>' +
      '<button class="btn"><i class="material-icons">close</i></button></div>';
   $('#searched-icon').html(data);
   $('#searched-icon').css('display', 'flex');
   $('#user').val('');
   $('#user').prop('disabled', true);
   $('#welcome').val('Hi ' + friendFirstName + ' ' + friendLastName + ', I would like to add you as a contact.')
}

$('#send-frnd-req-btn').on('click', function(){
	sendFriendRequest(this);
});

function sendFriendRequest(element) {
    const message = $('#welcome').val().trim();
    if(friendUserId == null || friendUserId == undefined) {
        $('#friend-req-submit-error').text('Please select a user');
        return;
    }
    if (message === '') {
        $('#friend-req-submit-error').text('Please enter a message');
        return;
    }
    if (message.length > 500) {
        $('#friend-req-submit-error').text('Message cannot exceed 500 characters');
        return;
    }
    $('#send-frnd-req-btn').prop('disabled', true);
    const payload = {
        "receiverId": friendUserId,
        "message": message
    }
    const socketReq = {
        "type" : "friendRequest",
        "payload" : payload
    }
    webSocket.send(JSON.stringify(socketReq));
}

$('#searched-icon').on('click', '.btn', function () {
   resetFriendReq();
   $('#friend-req-submit-error').html('&nbsp;');
});

$(document).on('click', '.fr-cancel-btn', function() {
   cancelFriendRequest(this);
});

function cancelFriendRequest(element) {
   let receiver = $(element).attr('id').replace('cancel-', '');
   $('#chat-' + receiver)
      .find('[data-toggle="tooltip"]')
      .tooltip('dispose')
      .end()
      .remove();
   $('#chatgroup-' + receiver).remove();
   const payload = {
      "receiverId": receiver
   }
   const socketReq = {
      "type": "cancelFriendRequest",
      "payload": payload
   }
   webSocket.send(JSON.stringify(socketReq));
}

$('#close-friend-req-btn').on('click', function(){
	resetFriendReq();
	$('#friend-req-submit-error').html('&nbsp;');
});

$(document).on('click', '.fr-accept-btn', function() {
   acceptFriendRequest(this);
});

$(document).on('click', '.fr-reject-btn', function() {
   rejectFriendRequest(this);
});

function acceptFriendRequest(element) {
   let senderId = $(element).attr('id').replace('accept-', '');
   acceptedSenderId = senderId;
   $(element).prop('disabled', true);
   const payload = {
      "senderId": senderId
   }
   const socketReq = {
      "type": "acceptFriendRequest",
      "payload": payload
   }
   webSocket.send(JSON.stringify(socketReq));
}

function rejectFriendRequest(element) {
    let senderId = $(element).attr('id').replace('reject-', '');
     $('#chat-' + senderId)
           .find('[data-toggle="tooltip"]')
           .tooltip('dispose')
           .end()
           .remove();
     $('#chatgroup-' + senderId).remove();
    rejectedSenderId = senderId;
    const payload = {
          "senderId": senderId
       }
       const socketReq = {
          "type" : "rejectFriendRequest",
          "payload": payload
       }
    webSocket.send(JSON.stringify(socketReq));
}