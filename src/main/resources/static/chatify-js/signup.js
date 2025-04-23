$('#submit-signup-btn').on('click', function(){
	processSignUp();
});

function processSignUp() {
    let firstName = $('#firstname').val().trim();
    let lastName = $('#lastname').val().trim();
    let email = $('#email').val().trim();
    let username = $('#username').val().trim();
    let password = $('#password').val().trim();
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if(!firstName) {
        $('#signup-submit-error').text("First name is required");
        return;
    }
    if(!lastName) {
        $('#signup-submit-error').text("Last name is required");
        return;
    }
    if (!email) {
        $('#signup-submit-error').text("Email is required");
        return;
    }
    if (!emailRegex.test(email)) {
       $('#signup-submit-error').text("Please provide a valid email address");
        return;
    }
    if (!username) {
        $('#signup-submit-error').text("Username is required");
        return;
    }
    if (!password) {
        $('#signup-submit-error').text("Password is required");
        return;
    }
    let reqBody = {
    	"email" : email,
    	"username" : username
    }
    $.ajax({
    		url : 'validate-signup',
    		type : 'POST',
    		data : JSON.stringify(reqBody),
    		contentType : 'application/json',
    		success : function(response) {
    		    console.log(JSON.stringify(response));
    			if (response.status == 100) {
    				$('#signup-form').submit();
    			} else {
                    $('#signup-submit-error').text(response.message);
    			}
    		},
    		error : function(errorThrown) {
    			console.log(JSON.stringify(errorThrown));
    			return;
    		}
    	});
}