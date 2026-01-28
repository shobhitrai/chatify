let friendUserId;
let friendFirstName;
let friendLastName;
let friendName;
let friendProfileImage;
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