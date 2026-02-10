function onlineNotification(payload) {
   if (payload.status === 100) {
      updateStatus(payload.data, true);
   } else {
      conlose.error("Failed to update online status:", payload.message);
   }
}

function offlineNotification(payload) {
   if (payload.status === 100) {
      updateStatus(payload.data, false);
   } else {
      console.error("Failed to update offline status:", payload.message);
   }
}

function updateStatus(contactId, isOnline) {
   const statusClassToAdd = isOnline ? 'online' : 'offline';
   const statusClassToRemove = isOnline ? 'offline' : 'online';

   $(`#contact-${contactId}`)
      .find('.status i')
      .removeClass(statusClassToRemove)
      .addClass(statusClassToAdd);

   if (chatOpenUserId === contactId) {
      $(`#chat-${contactId}`)
         .find('.status i')
         .removeClass(statusClassToRemove)
         .addClass(statusClassToAdd);
   }

   let $chat = $('#chatgroup-' + contactId);
    if ($chat.length) {
        $chat.find('.status i')
            .removeClass(statusClassToRemove)
            .addClass(statusClassToAdd);
    }

}


function appendNotification(payload) {
   if (payload.status === 100) {
      const noti = payload.data;
      const text = `
      <a href="#" class="filterNotifications all ${noti.isRecent ? 'latest' : 'oldest'} notification">
        <img class="avatar-md" src="${noti.senderProfileImage}" data-toggle="tooltip" data-placement="top"
            title="${noti.senderFirstName}" alt="avatar">
        <div class="status"><i class="material-icons online">fiber_manual_record</i></div>
        <div class="data">
          <p>${noti.message}</p>
          <span>${noti.formattedDate}</span>
        </div>
      </a>
      `;
      $('#alerts').prepend(text);
   } else {
      console.error('Failed to append notification: ' + payload.message);
   }
}