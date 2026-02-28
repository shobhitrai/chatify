$(document).on('click', '.clear-history-btn', function () {
    let senderId = $(this).closest(".babble").attr("id").replace("chat-", "");

});