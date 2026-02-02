let friendUserId = null;
let friendFirstName = null;
let friendLastName = null;
let friendName = null;
let friendProfileImage = null;
const userId = sessionUserId;

function resetFriendReq() {
     $('#searched-icon').html('');
     $('#welcome').val('');
     $('#user').prop('disabled', false);
     friendUserId = null;
     friendName = null;
     friendFirstName = null;
     friendLastName = null;
     friendProfileImage = null;
}