function receivedTextMessage(payload) {
   if (payload.status === 100) {
      const senderId = payload.data.senderId;
      if (chatOpenUserId === senderId) {
         const message = payload.data.message;
         const time = globalGetChatTimeByTimeStamp(payload.data.createdAt);
         const context = `
            <div class="message">
              <img class="avatar-md" src="${chatOpenProfileImage}" data-toggle="tooltip" data-placement="top"
                    title="${chatOpenFirstName}" alt="avatar">
              <div class="text-main">
                <div class="text-group">
                  <div class="text">
                    <p>${message}</p>
                  </div>
                </div>
                <span>${time}</span>
              </div>
            </div>
         `;
         globalAppendMessage(context);
      }
   } else {
      console.error("Error receiving text message: " + payload.message);
   }
}

function createMainChat(payload) {
   if (payload.status === 100) {
      let sender = payload.data.sender;
      let chat = payload.data.chat;
      let onlineStatus = sender.isOnline ? 'online' : 'offline';
      const context = `
      <a id="chatgroup-${sender.userId}" href="#" class="filterDiscussions all unread single">
        <img class="avatar-md" src="${sender.profileImage}" data-toggle="tooltip"
            data-placement="top" title="${sender.firstName}" alt="avatar">
        <div class="status"><i class="material-icons ${onlineStatus}">fiber_manual_record</i></div>
        <div class="new bg-gray"><span>?</span></div>
        <div class="data">
          <h5>${sender.firstName} ${sender.lastName}</h5>
          <span>${chat.formattedDate}</span>
          <p id="p-${sender.userId}">${chat.message}</p>
        </div>
      </a>
      `;
      $('#chats').prepend(context);
   }
}

function ackGetChat(payload) {
   if (payload.status === 100) {
      const contact = payload.data.otherUser;
      if (contact.userId === chatOpenUserId) {
         initializeChatVariables(contact);
         if (payload.data.chat.length === 1 && payload.data.chat[0].type === 'friendRequest') {
            appendFriendRequestChat(payload.data);
         } else {
            loadChatMessages(payload.data);
         }
      } else {
         console.warn("Received chat data for a different user than currently open.");
         alert("Something went wrong while opening the chat. Please reload the page.");
      }
   } else {
      console.error("Error getting chat: " + payload.message);
      alert(payload.message);
   }
}

function initializeChatVariables(contact) {
    chatOpenProfileImage = contact.profileImage;
    chatOpenFirstName = contact.firstName;
    chatOpenLastName = contact.lastName;
    chatOpenFullName = contact.firstName + ' ' + contact.lastName;
}

function appendFriendRequestChat(data) {
   const other = data.otherUser;
   const chat = data.chat[0];
   const onlineStatus = other.isOnline ? 'online' : 'offline';

   let requestText = '';
   let actionButtons = '';

   if (sessionUserId === chat.senderId) {
      requestText = `You have sent a friend request to ${other.firstName} ${other.lastName}, Waiting for acceptance.`;
      actionButtons =
         `<button class="btn button fr-cancel-btn" id="cancel-${other.userId}">
         <i class="material-icons">close</i>
       </button>`;
   } else {
      requestText = chat.message;
      actionButtons =
         `<button class="btn button fr-accept-btn" id="accept-${other.userId}">
         <i class="material-icons">check</i>
       </button>
       <button class="btn button fr-reject-btn" id="reject-${other.userId}">
         <i class="material-icons">close</i>
       </button>`;
   }

   const context = `
       <div class="babble tab-pane fade active show" id="chat-${other.userId}">
          <div class="chat">
             <div class="top">
                <div class="container">
                   <div class="col-md-12">
                      <div class="inside">
                         <a href="#"><img class="avatar-md" src="${other.profileImage}" data-toggle="tooltip" title="${other.firstName}"></a>
                         <div class="status"><i class="material-icons ${onlineStatus}">fiber_manual_record</i></div>
                         <div class="data">
                            <h5>${other.firstName} ${other.lastName}</h5>
                            <span>Inactive</span>
                         </div>
                         <button class="btn disabled d-md-block d-none" disabled><i class="material-icons md-30">phone_in_talk</i></button>
                         <button class="btn disabled d-md-block d-none" disabled><i class="material-icons md-36">videocam</i></button>
                         <button class="btn disabled d-md-block d-none" disabled><i class="material-icons md-30">info</i></button>
                         <div class="dropdown">
                            <button class="btn disabled" disabled><i class="material-icons md-30">more_vert</i></button>
                         </div>
                      </div>
                   </div>
                </div>
             </div>
             <div class="content empty">
                <div class="container">
                   <div class="col-md-12">
                      <div class="no-messages request">
                         <a href="#"><img class="avatar-xl" src="${other.profileImage}" data-toggle="tooltip" title="${other.firstName}"></a>
                         <h5><span>${requestText}</span></h5>
                         <div class="options">${actionButtons}</div>
                      </div>
                   </div>
                </div>
             </div>
             <div class="container">
                <div class="col-md-12">
                   <div class="bottom">
                      <form class="position-relative w-100">
                         <textarea class="form-control" placeholder="Messaging unavailable" rows="1" disabled></textarea>
                         <button class="btn emoticons disabled" disabled><i class="material-icons">insert_emoticon</i></button>
                         <button class="btn send disabled" disabled><i class="material-icons">send</i></button>
                      </form>
                      <label>
                      <input type="file" disabled>
                      <span class="btn attach disabled d-sm-block d-none"><i class="material-icons">attach_file</i></span>
                      </label>
                   </div>
                </div>
             </div>
          </div>
       </div>
       `;
   $('#nav-tabContent').empty().append(context);
}

function loadChatMessages(data) {
   let other = data.otherUser;
   let chat = data.chat;
   const status = other.isOnline ? 'online' : 'offline';
   const status2 = other.isOnline ? 'Active' : 'Inactive';

   let chatWindow = '';
   if (chat.length > 0) {
        let messageContent = '';
      for (let i = 0; i < chat.length; i++) {
         if(chat[i].senderId === sessionUserId) {
            messageContent += `
            <div class="message me">
               <div class="text-main">
                  <div class="text-group me">
                     <div class="text me">
                        <p>${chat[i].message}</p>
                     </div>
                  </div>
                  <span>${globalGetChatTimeByTimeStamp(chat[i].createdAt)}</span>
               </div>
            </div>
            `;
         } else {
            messageContent += `
                <div class="message">
                   <img class="avatar-md" src="${chatOpenProfileImage}" data-toggle="tooltip" data-placement="top" title="${chatOpenFirstName}" alt="avatar">
                   <div class="text-main">
                      <div class="text-group">
                         <div class="text">
                            <p>${chat[i].message}</p>
                         </div>
                      </div>
                      <span>${globalGetChatTimeByTimeStamp(chat[i].createdAt)}</span>
                   </div>
                </div>
            `;
         }
      }
      chatWindow = `
        <div class="content">
           <div class="container">
              <div class="col-md-12">
                    ${messageContent}
              </div>
           </div>
        </div>
      `;
   } else {
      chatWindow = `
        <div class="content empty">
           <div class="container">
              <div class="col-md-12">
                 <div class="no-messages">
                    <i class="material-icons md-48">forum</i>
                    <p>Seems people are shy to start the chat. Break the ice send the first message.</p>
                 </div>
              </div>
           </div>
        </div>
      `;
   }

   const context = `
    <div class="babble tab-pane fade active show" id="chat-${other.userId}">
      <div class="chat">
        <div class="top">
          <div class="container">
            <div class="col-md-12">
              <div class="inside">
                <a href="#"><img class="avatar-md" src="${other.profileImage}" data-toggle="tooltip"
                data-placement="top" title="${other.firstName}" alt="avatar"></a>
                <div class="status"><i class="material-icons ${status}">fiber_manual_record</i></div>
                <div class="data"><h5><a href="#">${other.firstName} ${other.lastName}</a></h5><span>${status2}</span></div>

                <button class="btn connect d-md-block d-none" name="2"><i class="material-icons md-30">phone_in_talk</i></button>
                <button class="btn connect d-md-block d-none" name="2"><i class="material-icons md-36">videocam</i></button>
                <button class="btn d-md-block d-none"><i class="material-icons md-30">info</i></button>

                <div class="dropdown">
                  <button class="btn" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <i class="material-icons md-30">more_vert</i>
                  </button>
                  <div class="dropdown-menu dropdown-menu-right">
                    <button class="dropdown-item connect" name="2"><i class="material-icons">phone_in_talk</i>Voice Call</button>
                    <button class="dropdown-item connect" name="2"><i class="material-icons">videocam</i>Video Call</button>
                    <hr>
                    <button class="dropdown-item"><i class="material-icons">clear</i>Clear History</button>
                    <button class="dropdown-item"><i class="material-icons">block</i>Block Contact</button>
                    <button class="dropdown-item"><i class="material-icons">delete</i>Delete Contact</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        ${chatWindow}
        <div class="container">
          <div class="col-md-12">
            <div class="bottom">
              <form class="position-relative w-100">
                <textarea class="form-control" placeholder="Start typing for reply..." rows="1"></textarea>
                <button class="btn emoticons"><i class="material-icons">insert_emoticon</i></button>
                <button type="button" class="btn send"><i class="material-icons">send</i></button>
              </form>
              <label>
                <input type="file">
                <span class="btn attach d-sm-block d-none"><i class="material-icons">attach_file</i></span>
              </label>
            </div>
          </div>
        </div>
      </div>

      <div class="call" id="call2">
        <div class="content">
          <div class="container">
            <div class="col-md-12">
              <div class="inside">
                <div class="panel">
                  <div class="participant">
                    <img class="avatar-xxl" src="${other.profileImage}" alt="avatar">
                    <span>Connecting</span>
                  </div>
                  <div class="options">
                    <button class="btn option"><i class="material-icons md-30">mic</i></button>
                    <button class="btn option"><i class="material-icons md-30">videocam</i></button>
                    <button class="btn option call-end"><i class="material-icons md-30">call_end</i></button>
                    <button class="btn option"><i class="material-icons md-30">person_add</i></button>
                    <button class="btn option"><i class="material-icons md-30">volume_up</i></button>
                  </div>
                  <button class="btn back" name="2"><i class="material-icons md-24">chat</i></button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    `;
   $('#nav-tabContent').empty().append(context);
}