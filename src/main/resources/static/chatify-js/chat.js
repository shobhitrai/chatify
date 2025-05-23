function createMainChat(payload) {
   if (payload.status === 100) {
        let cg = payload.data.chatGroups;

        let data = '<div class="babble tab-pane fade" id="chat-' + cg.senderId + '">' +
        '<div class="chat"> <div class="top"> <div class="container"> <div class="col-md-12">'+
        ' <div class="inside"> <a href="#"><img class="avatar-md" src="'+cg.senderProfileImage+'" '+
        'data-toggle="tooltip" data-placement="top" title="'+cg.senderFirstName+'" alt="avatar"></a>'+
        ' <div class="status"> <i class="material-icons offline">fiber_manual_record</i>'+
        ' </div> <div class="data"> <h5><a href="#">'+cg.senderFirstName+' '+cg.senderLastName+'</a></h5>'+
        ' <span>Inactive</span> </div> <button class="btn disabled d-md-block d-none" disabled>'+
        '<i class="material-icons md-30">phone_in_talk</i></button> <button class="btn disabled '+
         'd-md-block d-none" disabled><i class="material-icons md-36">videocam</i></button> <button '+
         'class="btn d-md-block disabled d-none" disabled><i class="material-icons md-30">info</i>'+
         '</button> <div class="dropdown"> <button class="btn disabled" data-toggle="dropdown" '+
         'aria-haspopup="true" aria-expanded="false" disabled><i class="material-icons md-30">more_vert'+
         '</i></button> <div class="dropdown-menu dropdown-menu-right"> <button class="dropdown-item">'+
         '<i class="material-icons">phone_in_talk</i>Voice Call</button> <button class="dropdown-item">'+
         '<i class="material-icons">videocam</i>Video Call</button> <hr> <button class="dropdown-item">'+
         '<i class="material-icons">clear</i>Clear History</button> <button class="dropdown-item">'+
         '<i class="material-icons">block</i>Block Contact</button> <button class="dropdown-item">'+
         '<i class="material-icons">delete</i>Delete Contact</button> </div> </div> </div> </div>'+
         ' </div> </div> <div class="content empty"> <div class="container"> <div class="col-md-12">'+
         ' <div class="no-messages request"> <a href="#"><img class="avatar-xl" '+
         'src="'+cg.senderProfileImage+'" data-toggle="tooltip" data-placement="top" '+
         'title="'+cg.senderFirstName+'" alt="avatar"></a> <h5><span>'+cg.chats[0].message+'</span></h5>'+
         ' <div class="options"> <button class="btn button fr-accept-btn" id="accept-'+cg.senderId+'">'+
         '<i class="material-icons">check</i></button> <button class="btn button fr-reject-btn" '+
         'id="reject-'+cg.senderId+'"><i class="material-icons">close</i></button> </div>'+
         ' </div> </div> </div> </div> <div class="container"> <div class="col-md-12">'+
         ' <div class="bottom"> <form class="position-relative w-100"> <textarea class="form-control"'+
         ' placeholder="Messaging unavailable" rows="1" disabled></textarea> <button class="btn'+
         ' emoticons disabled" disabled><i class="material-icons">insert_emoticon</i></button>'+
         ' <button class="btn send disabled" disabled><i class="material-icons">send</i></button>'+
         ' </form> <label> <input type="file" disabled> <span class="btn attach disabled '+
         'd-sm-block d-none"><i class="material-icons">attach_file</i></span></label></div>'+
         ' </div></div></div>';

         $('#nav-tabContent').append(data);

   }
}