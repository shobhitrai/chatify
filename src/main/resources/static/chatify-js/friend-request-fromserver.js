function ackSearchedUsers(payload) {
   $('#searched-user').html('');
   let text = '<div class="dropdown-menu" id="user-dropdown">'
   if (payload.status === 100) {
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

function ackAcceptFriendRequest(payload) {
   $('#accept-' + acceptedSenderId).prop('disabled', false);
   if (payload.status === 100) {
      const contact = payload.data;
      addToContactList(contact);
      updateMainChat(contact);
   } else {
      console.log(payload.message);
   }
}

function addContact(payload) {
   if (payload.status === 100) {
      const contact = payload.data;
      addToContactList(contact);
      if(contact.contactId === chatOpenUserId) {
        updateMainChat(contact);
      }
   } else {
      console.log(payload.message)
   }
}

function removeContact(payload) {
   const removedContactId = payload.data;
   if (payload.status === 100) {
      $('#chat-' + removedContactId).remove();
      $('#chatgroup-' + removedContactId).remove();
   } else {
      console.log(payload.message);
   }
}

function addToContactList(contact) {
   const status = contact.isOnline ? 'online' : 'offline';
   const data = `
   <a href="#" id="contact-${contact.contactId}" class="filterMembers all ${status} contact">
     <img class="avatar-md" src="${contact.profileImage}" data-toggle="tooltip" data-placement="top"
        title="${contact.firstName}" alt="avatar">
     <div class="status"><i class="material-icons ${status}">fiber_manual_record</i></div>
     <div class="data">
       <h5>${contact.firstName} ${contact.lastName}</h5>
       <p>Sofia, Bulgaria</p>
     </div>
     <div class="person-add"><i class="material-icons">person</i></div>
   </a>`;
   $('#contacts').prepend(data);
}

function ackFriendRequest(payload) {
   $('#send-frnd-req-btn').prop('disabled', false);
   if (payload.status == 100) {
      $('#friend-req-submit-error').text('Friend request sent successfully to ' + friendName);
      appendFriendRequestToChat(payload.data);
      resetFriendReq();
   } else {
      $('#friend-req-submit-error').text(payload.message);
   }
}

function appendFriendRequestToChat(isOnline) {
   const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
   const now2 = new Date();
   const formatted2 = `${months[now2.getMonth()]} ${now2.getDate()}`;
   const chatgroupMessage = "You have send a friend request to " + friendFirstName + ' ' + friendLastName;
   const onlineStatus = isOnline ? 'online' : 'offline';
   const data = `
   <a id="chatgroup-${friendUserId}" href="#" class="filterDiscussions all unread single">
     <img class="avatar-md" src="${friendProfileImage}" data-toggle="tooltip" data-placement="top" title="${friendFirstName}" alt="avatar">
     <div class="status"><i class="material-icons ${onlineStatus}">fiber_manual_record</i></div>
     <div class="new bg-gray"><span>?</span></div>
     <div class="data">
       <h5>${friendFirstName} ${friendLastName}</h5>
       <span>${formatted2}</span>
       <p id="p-${friendUserId}">${chatgroupMessage}</p>
     </div>
   </a>
   `;
   $('#chats').prepend(data);
}

function updateMainChat(contact) {
    const status = contact.isOnline ? 'online' : 'offline';
    const status2 = contact.isOnline ? 'Active' : 'Inactive';
    const context = `
    <div class="babble tab-pane fade active show" id="chat-${contact.userId}">
      <div class="chat">
        <div class="top">
          <div class="container">
            <div class="col-md-12">
              <div class="inside">
                <a href="#"><img class="avatar-md" src="${contact.profileImage}" data-toggle="tooltip" data-placement="top" title="${contact.firstName}" alt="avatar"></a>
                <div class="status"><i class="material-icons ${status}">fiber_manual_record</i></div>
                <div class="data"><h5><a href="#">${contact.firstName} ${contact.lastName}</a></h5><span>${status2}</span></div>

                <button class="btn connect d-md-block d-none" name="2"><i class="material-icons md-30">phone_in_talk</i></button>
                <button class="btn connect d-md-block d-none" name="2"><i class="material-icons md-36">videocam</i></button>
                <button class="btn d-md-block d-none"><i class="material-icons md-30">info</i></button>

                <div class="dropdown">
                  <button class="btn" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i class="material-icons md-30">more_vert</i></button>
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
                    <img class="avatar-xxl" src="${contact.profileImage}" alt="avatar">
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

    $('#nav-tabContent').html('');
    $('#nav-tabContent').append(context);
}