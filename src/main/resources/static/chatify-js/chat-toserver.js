$(document).on('click', '.filterDiscussions', function (e) {
   e.preventDefault();
   const contactId = $(this).attr('id').replace('chatgroup-', '');
   getAllChats(contactId);
});

$(document).on('click', '.filterMembers', function (e) {
   e.preventDefault();
   const contactId = $(this).attr('id').replace('contact-', '');
   getAllChats(contactId)
});

$(document).on('click', '.bottom .send', async function () {
   const $btn = $(this);
   const $textarea = $btn.closest('.bottom').find('textarea');

   let message = $textarea.val().trim();
   if (!message) return;
   console.log('Message to send:', message);
   $btn.prop('disabled', true);

   globalCreateChatGroupIfNotExists(chatOpenUserId, new Date(), message);
   $('#chatgroup-' + chatOpenUserId).find('.data p').text(message);

   $textarea.val('');
   const context = `
           <div class="message me">
              <div class="text-main">
                 <div class="text-group me">
                    <div class="text me">
                       <p>${message}</p>
                    </div>
                 </div>
                 <span>${globalGetChatTimeByDate(new Date())}</span>
              </div>
           </div>
           `;
   globalAppendMessage(context);

   $('#chatgroup-' + chatOpenUserId).find('.data p').text(message);

   try {
      await sendMessageToSocket(message);
   } catch (err) {
      alert('Message failed to send');
      console.error(err);
   } finally {
      $btn.prop('disabled', false);
   }
});

$(document).on('keypress', '.bottom textarea', function (e) {
   if (e.which === 13 && !e.shiftKey) {
      e.preventDefault();
      $(this).closest('.bottom').find('.send').click();
   }
});

$(document).on('input', '.bottom textarea', function () {
   const maxLength = 500;
   if (this.value.length > maxLength) {
      this.value = this.value.substring(0, maxLength);
   }
});

function sendMessageToSocket(message) {
   const payload = {
      "receiverId": chatOpenUserId,
      "message": message
   }
   const socketReq = {
      "type": "textMessage",
      "payload": payload
   }
   //Promise to handle send success/failure
   return globalSendToSocket(socketReq);
}

function getAllChats(contactId) {
   if (chatOpenUserId === contactId) {
      return;
   }
   chatOpenUserId = contactId;
   $('#chatgroup-' + chatOpenUserId).find('div.new').remove();
   const color = $('#chatgroup-' + chatOpenUserId).find('.data p').css('color');
   if (color === 'rgb(33, 37, 41)') {
      $('#chatgroup-' + chatOpenUserId).find('.data p').css('color', '#bdbac2');
   }
   sendPayloadToGetChat(contactId);
}

function sendPayloadToGetChat(contactId) {
   const payload = {
      "contactId": contactId
   }
   const socketReq = {
      "type": "getChat",
      "payload": payload
   }
   webSocket.send(JSON.stringify(socketReq));
}