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
   globalAdjustScroll();
}

function globalAdjustScroll() {
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

function addUnreadChatCount(contactId) {
   const $chat = $('#chatgroup-' + contactId);
   if (!$chat.length) return;

   let $badge = $chat.find('.new');
   if ($badge.length === 0) {
      $badge = $(`
      <div class="new bg-green">
        <span>1</span>
      </div>
    `);

      $chat.find('.data').before($badge);
   } else {
      $badge.removeClass('bg-gray').addClass('bg-green');
      const $span = $badge.find('span');
      const count = parseInt($span.text(), 10) || 0;
      $span.text(count + 1);
   }
}

function globalCreateChatGroupIfNotExists(contactId, timestamp, message) {
   let $chat = $('#chatgroup-' + contactId);
   if ($chat.length === 0) {
      const $contact = $('#contact-' + contactId);
      if ($contact.length) {
         const fullName = $contact.find('.data h5').text().trim();
         const [firstName, ...rest] = fullName.split(' ');
         const lastName = rest.join(' ');
         const isOnline = $contact.hasClass('online');
         const profileImage = $contact.find('img.avatar-md').attr('src');
         let onlineStatus = isOnline ? 'online' : 'offline';
         const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
         const formattedDate = `${months[timestamp.getMonth()]} ${timestamp.getDate()}`;

         const context = `
               <a id="chatgroup-${contactId}" href="#" class="filterDiscussions all unread single">
                 <img class="avatar-md" src="${profileImage}" data-toggle="tooltip"
                     data-placement="top" title="${firstName}" alt="avatar">
                 <div class="status"><i class="material-icons ${onlineStatus}">fiber_manual_record</i></div>
                 <div class="data">
                   <h5>${firstName} ${lastName}</h5>
                   <span>${formattedDate}</span>
                   <p></p>
                 </div>
               </a>
               `;
         $('#chats').prepend(context);
      }
   }
}
