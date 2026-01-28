let typingTimer; // Timer identifier
const typingDelay = 200; // Delay in milliseconds
let friendUserId;
let friendFirstName;
let friendLastName;
let friendName;
let friendProfileImage;
const userId = sessionUserId;

$('#send-frnd-req-btn').on('click', function(){
	sendFriendRequest(this);
});

$(document).on('click', '.fr-cancel-btn', function() {
   cancelFriendRequest(this);
});

$('#close-friend-req-btn').on('click', function(){
	resetFriendReq();
	$('#friend-req-submit-error').html('&nbsp;');
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

function appendFriendRequestToChat() {
   const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
   const now2 = new Date();
   const formatted2 = `${months[now2.getMonth()]} ${now2.getDate()}`;
   const chatgroupMessage = 'You have sent a friend request to ' + friendFirstName + ' ' + friendLastName + '.';
   const requestMessage = chatgroupMessage + '. Waiting for acceptance.';

   let data = '<div class="babble tab-pane fade" id="chat-' + friendUserId + '">' +
      '<div class="chat"> <div class="top"> <div class="container"> <div class="col-md-12">' +
      ' <div class="inside"> <a href="#"><img class="avatar-md" src="' + friendProfileImage + '" ' +
      'data-toggle="tooltip" data-placement="top" title="' + friendFirstName + '" alt="avatar"></a>' +
      ' <div class="status"> <i class="material-icons offline">fiber_manual_record</i>' +
      ' </div> <div class="data"> <h5><a href="#">' + friendFirstName + ' ' + friendLastName + '</a></h5>' +
      ' <span>Inactive</span> </div> <button class="btn disabled d-md-block d-none" disabled>' +
      '<i class="material-icons md-30">phone_in_talk</i></button> <button class="btn disabled ' +
      'd-md-block d-none" disabled><i class="material-icons md-36">videocam</i></button> <button ' +
      'class="btn d-md-block disabled d-none" disabled><i class="material-icons md-30">info</i>' +
      '</button> <div class="dropdown"> <button class="btn disabled" data-toggle="dropdown" ' +
      'aria-haspopup="true" aria-expanded="false" disabled><i class="material-icons md-30">more_vert' +
      '</i></button> <div class="dropdown-menu dropdown-menu-right"> <button class="dropdown-item">' +
      '<i class="material-icons">phone_in_talk</i>Voice Call</button> <button class="dropdown-item">' +
      '<i class="material-icons">videocam</i>Video Call</button> <hr> <button class="dropdown-item">' +
      '<i class="material-icons">clear</i>Clear History</button> <button class="dropdown-item">' +
      '<i class="material-icons">block</i>Block Contact</button> <button class="dropdown-item">' +
      '<i class="material-icons">delete</i>Delete Contact</button> </div> </div> </div> </div>' +
      ' </div> </div> <div class="content empty"> <div class="container"> <div class="col-md-12">' +
      ' <div class="no-messages request"> <a href="#"><img class="avatar-xl" ' +
      'src="' + friendProfileImage + '" data-toggle="tooltip" data-placement="top" ' +
      'title="' + friendFirstName + '" alt="avatar"></a> <h5><span>' + requestMessage + '</span></h5>' +
      ' <div class="options"><button class="btn button fr-cancel-btn" ' +
      'id="cancel-' + friendUserId + '"><i class="material-icons">close</i></button> </div>' +
      ' </div> </div> </div> </div> <div class="container"> <div class="col-md-12">' +
      ' <div class="bottom"> <form class="position-relative w-100"> <textarea class="form-control"' +
      ' placeholder="Messaging unavailable" rows="1" disabled></textarea> <button class="btn' +
      ' emoticons disabled" disabled><i class="material-icons">insert_emoticon</i></button>' +
      ' <button class="btn send disabled" disabled><i class="material-icons">send</i></button>' +
      ' </form> <label> <input type="file" disabled> <span class="btn attach disabled ' +
      'd-sm-block d-none"><i class="material-icons">attach_file</i></span></label></div>' +
      ' </div></div></div>';

   $('#nav-tabContent').append(data);

   data = '<a id="chatgroup-' + friendUserId + '" ' +
      'href="#chat-' + friendUserId + '" class="filterDiscussions all unread single" ' +
      'data-toggle="list"><img class="avatar-md" src="' + friendProfileImage + '" ' +
      'data-toggle="tooltip" data-placement="top" title="' + friendFirstName + '" ' +
      'alt="avatar"><div class="status"><i class="material-icons offline">' +
      'fiber_manual_record</i></div><div class="new bg-gray"><span>?</span></div>' +
      '<div class="data"><h5>' + friendFirstName + ' ' + friendLastName + '</h5>' +
      '<span>' + formatted2 + '</span><p id="p-' + friendUserId + '">' +
      chatgroupMessage + '</p></div></a>';
   $('#chats').prepend(data);
}

function ackFriendRequest(payload) {
    $('#send-frnd-req-btn').prop('disabled', false);
    if (payload.status == 100) {
        $('#friend-req-submit-error').text('Friend request sent successfully to ' + friendName);
        appendFriendRequestToChat();
        resetFriendReq();
    } else {
        $('#friend-req-submit-error').text(payload.message);
    }
}

$('#user').on('keyup', function (e) {
     clearTimeout(typingTimer); // Clear the previous timer
     const element = this;
     typingTimer = setTimeout(function () {
        getSearchedUsers(e, element); // Call the function after the delay
     }, typingDelay);
});

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

$('#searched-icon').on('click', '.btn', function () {
   resetFriendReq();
   $('#friend-req-submit-error').html('&nbsp;');
});

function resetFriendReq() {
     $('#searched-icon').html('');
     $('#welcome').val('');
     $('#user').prop('disabled', false);
     friendUserId = null;
     friendName = null;
}

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

function ackSearchedUsers(payload) {
   $('#searched-user').html('');
   let text = '<div class="dropdown-menu" id="user-dropdown">'
   if (payload.status === 100) {
      for (let i = 0; i < payload.data.length; i++) {
         const item = payload.data[i];
         text += '<button class="dropdown-item searched-user-btn" type="button"' +
            'data-firstname="' + item.firstName + '"' +
            'data-lastname="' + item.lastName + '"' +
            'data-userid="' + item.userId + '"' +
            'data-profileimage="' + item.profileImage + '">' +
            item.username + '</button>'
      }
   } else {
      text += '<button class="dropdown-item" type="button">No user found</button>'
   }
   text += '</div>'
   $('#searched-user').html(text);
}

function appendNotification(payload) {
   if (payload.status === 100) {
      let noti = payload.data;
      let text = '<a href="#" class="filterNotifications all ' +
         (noti.isRecent ? 'latest' : 'oldest') + ' notification"' +
         ' data-toggle="list"><img class="avatar-md" src="' + noti.senderProfileImage + '" data-toggle="tooltip"' +
         ' data-placement="top" title="' + noti.senderFirstName + '" alt="avatar">' +
         '<div class="status"><i class="material-icons online">' +
         ' fiber_manual_record</i></div><div class="data"><p>' + noti.message +
         '</p><span>' + noti.formattedDate + '</span></div></a>';

      $('#alerts').prepend(text);
   } else {
      console.error('Failed to append notification: ' + payload.message);
   }
}