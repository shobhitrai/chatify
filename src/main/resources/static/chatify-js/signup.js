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
       $('#signup-submit-error').text("Please provide a valid email address.");
        return;
    }
    if (!password) {
        $('#signup-submit-error').text("Password is required.");
        return;
    }
    $.ajax({
    		url : 'check-email-exist/' + email,
    		type : 'GET',
    		contentType : 'application/json',
    		success : function(response) {
    			if (response.code == 100) {
    				$('#signup-form').submit();
    			} else {
                    $('#signup-submit-error').text("Email already exists.");
    			}
    		},
    		error : function(errorThrown) {
    			console.log(JSON.stringify(errorThrown));
    			return;
    		}
    	});
}