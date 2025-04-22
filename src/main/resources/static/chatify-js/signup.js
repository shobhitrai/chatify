$('#submit-signup-btn').on('click', function(){
	processSignUp();
});

function processSignUp() {
    let name = $('#name').val().trim();
    let email = $('#email').val().trim();
    let password = $('#password').val().trim();
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!name) {
        $('#signup-submit-error').text("Name is required.");
        return;
    }
    if (!email) {
        $('#signup-submit-error').text("Email is required.");
        return;
    }
    if (!emailRegex.test(email)) {
        $('#signup-submit-error').text("Invalid email format.");
        return;
    }
    if (!password) {
        $('#signup-submit-error').text("Password is required.");
        return;
    }
    $('#signup-form').submit();
}