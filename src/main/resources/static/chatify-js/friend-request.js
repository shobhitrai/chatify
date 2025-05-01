let typingTimer; // Timer identifier
const typingDelay = 200; // Delay in milliseconds
let friendUserId;
let friendName;
const userId = sessionUserId;

$('#send-frnd-req-btn').on('click', function(){
	sendFriendRequest(this)
});

$('#close-friend-req-btn').on('click', function(){
	resetFriendReq();
	$('#friend-req-submit-error').html('&nbsp;');
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

function ackFriendRequest(payload) {
    $('#send-frnd-req-btn').prop('disabled', false);
    if (payload.status == 100) {
        $('#friend-req-submit-error').text('Friend request sent successfully to ' + friendName);
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
   const firstName = $(element).data('firstname');
   const lastName = $(element).data('lastname');
   friendUserId = $(element).data('userid');
   friendName = firstName + ' ' + lastName;
   const profileImage = $(element).data('profileimage');
   $('#searched-user').html('');
   var data = '<div class="user" id="contact">' +
      '<img class="avatar-sm" src="' + profileImage + '" alt="avatar">' +
      '<h5>' + firstName + ' ' + lastName + '</h5>' +
      '<button class="btn"><i class="material-icons">close</i></button></div>';
   $('#searched-icon').html(data);
   $('#searched-icon').css('display', 'flex');
   $('#user').val('');
   $('#user').prop('disabled', true);
   $('#welcome').val('Hi ' + firstName + ' ' + lastName + ', I would like to add you as a contact.')
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
      e.which == 8 ||
      e.which == 46) {
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
   if (payload.status == 100) {
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