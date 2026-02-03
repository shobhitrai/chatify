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

function globalAppendMessage(contextHtml) {
   const $content = $('.content');
   const $messageContainer = $content.find('.container .col-md-12');

   if ($content.hasClass('empty')) {
      $content.removeClass('empty');
      $messageContainer.find('.no-messages').remove();
   }

   $messageContainer.append(contextHtml);
   const ok = document.querySelector('.main .chat .content');
     ok.scrollTop = ok.scrollHeight - ok.clientHeight;
}

function globalGetChatTimeByTimeStamp(timestamp) {
   const date = new Date(timestamp);
   const time = date.toLocaleTimeString('en-IN', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: true
   }).toUpperCase();
   return time;
}

function globalGetChatTimeByDate(dateObj) {
   const time = dateObj.toLocaleTimeString('en-IN', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: true
   }).toUpperCase();
   return time;
}

