let typingTimer; // Timer identifier
const typingDelay = 200; // Delay in milliseconds

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
   const userId = $(element).data('userid');
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
    $('#searched-icon').html('');
    $('#welcome').val('');
    $('#user').prop('disabled', false);
});

function getSearchedUsers(e, element) {
   if ((e.which >= 48 && e.which <= 57) ||
      (e.which >= 65 && e.which <= 90) ||
      (e.which >= 97 && e.which <= 122) ||
      e.which == 8 ||
      e.which == 46) {
      let username = $(element).val().trim();
      if (username !== '') {
         let reqBody = {
            "username": username
         }
         $.ajax({
            url: 'search-users',
            type: 'POST',
            data: JSON.stringify(reqBody),
            contentType: 'application/json',
            success: function (response) {
               console.log(JSON.stringify(response));
               $('#searched-user').html('');
               let text = '<div class="dropdown-menu" id="user-dropdown">'
               if (response.status == 100) {
                  for (let i = 0; i < response.data.length; i++) {
                     const item = response.data[i];
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
            },
            error: function (errorThrown) {
               console.log(JSON.stringify(errorThrown));
               return;
            }
         });
      }
   }
}