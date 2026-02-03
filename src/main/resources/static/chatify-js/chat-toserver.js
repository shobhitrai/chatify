$(document).on('click', '.filterDiscussions', function (e) {
   e.preventDefault();
   chatClicked(this);
});

$(document).on('click', '.filterMembers', function (e) {
   e.preventDefault();
   openChat(this);
});

$(document).on('click', '.bottom .send', async function () {
   const $btn = $(this);
   const $textarea = $btn.closest('.bottom').find('textarea');

   let message = $textarea.val().trim();
   if (!message) return;
   console.log('Message to send:', message);
   $btn.prop('disabled', true);

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

function chatClicked(element) {
   const contactId = $(element).attr('id').replace('chatgroup-', '');
   chatOpenUserId = contactId;
   let color = $('#p-' + contactId).css('color');
   if (color === 'rgb(33, 37, 41)') {
      $('#p-' + contactId).attr('style', 'color: #bdbac2;');
   }
   sendPayloadToGetChat(contactId);
}

function openChat(element) {
   const contactId = $(element).attr('id').replace('contact-', '');
   chatOpenUserId = contactId;
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