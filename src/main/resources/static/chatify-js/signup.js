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
//    if (!emailRegex.test(email)) {
//       $('#signup-submit-error').text("Please provide a valid email address.");
//        return;
//    }
    $.ajax({
    		url : 'check-email-exist/' + email,
    		type : 'GET',
    		contentType : 'application/json',
    		success : function(response) {
    			alert(JSON.stringify(response));
    			if (response.code == 101) {
    				$('#signup-submit-error').text("Email already exists.");
    			} else {
    				   if (!password) {
                            $('#signup-submit-error').text("Password is required.");
                            return;
                        }
                        $('#signup-form').submit();
    			}
    		},
    		error : function(errorThrown) {
    			console.log(JSON.stringify(errorThrown));
    			alert('Please refresh the page and try again.');
    			return;
    		}
    	});
}