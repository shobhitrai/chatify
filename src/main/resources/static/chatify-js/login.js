$('#email').val('shobhit@gmail.com');
$('#password').val('hello');

$('#submit-login-btn').on('click', function(){
	processLogin();
});

function processLogin() {
    let email = $('#email').val().trim();
    let password = $('#password').val().trim();
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!email) {
        $('#login-submit-error').text("Email is required.");
        return;
    }
    if (!emailRegex.test(email)) {
       $('#login-submit-error').text("Please provide a valid email address.");
        return;
    }
    if (!password) {
        $('#login-submit-error').text("Password is required.");
        return;
    }
    $('#login-form').submit();
}