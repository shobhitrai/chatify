<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="path" value="${pageContext.servletContext.contextPath}"></c:set>
<!DOCTYPE html>
<html lang="en">

<head>
		<meta charset="utf-8">
		<title>Chatify</title>
		<meta name="description" content="#">
		<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
		<!-- Bootstrap core CSS -->
        <link href="${path}/css/lib/bootstrap.min.css" type="text/css" rel="stylesheet">
        <!-- Swipe core CSS -->
        <link href="${path}/css/swipe.min.css" type="text/css" rel="stylesheet">
        <!-- Favicon -->
        <link href="${path}/image/favicon.png" type="image/png" rel="icon">
	</head>
	<body class="start">
		<main>
			<div class="layout">
				<!-- Start of Sign Up -->
				<div class="main order-md-2">
					<div class="start">
						<div class="container">
							<div class="col-md-12">
								<div class="content">
									<h1>Create Account</h1>
									<div class="third-party">
										<button class="btn item bg-blue">
											<i class="material-icons">pages</i>
										</button>
										<button class="btn item bg-teal">
											<i class="material-icons">party_mode</i>
										</button>
										<button class="btn item bg-purple">
											<i class="material-icons">whatshot</i>
										</button>
									</div>
									<p>or use your email for registration:</p>
									<form action="signup" class="signup" id="signup-form" method="post">
										<div class="form-parent">
											<div class="form-group">
												<input type="text" id="firstname" name="firstname" class="form-control" placeholder="First Name">
												<button class="btn icon"><i class="material-icons">person_outline</i></button>
											</div>
											<div class="form-group">
                                                <input type="text" id="lastname" name="lastname" class="form-control" placeholder="Last Name">
                                                <button class="btn icon"><i class="material-icons">person_outline</i></button>
                                            </div>
										</div>
										<div class="form-group">
                                            <input type="email" id="email" name="email" class="form-control" placeholder="Email Address">
                                            <button class="btn icon"><i class="material-icons">mail_outline</i></button>
                                        </div>
										<div class="form-group">
                                            <input type="test" id="username" name="username" class="form-control" placeholder="Username">
                                            <button class="btn icon"><i class="material-icons">person_outline</i></button>
                                        </div>
										<div class="form-group">
											<input type="password" id="password" name="password" class="form-control" placeholder="Password">
											<button class="btn icon"><i class="material-icons">lock_outline</i></button>
										</div>
										<button type="button" class="btn button" id="submit-signup-btn">Sign Up</button>
										<div>
                                           <p style="margin-top: 5px; color: red;" id="signup-submit-error">&nbsp;${error}</p>
                                        </div>
										<div class="callout">
											<span>Already a member? <a href="login">Sign In</a></span>
										</div>
									</form>
								</div>
							</div>
						</div>
					</div>
				</div>
				<!-- End of Sign Up -->
				<!-- Start of Sidebar -->
				<div class="aside order-md-1">
					<div class="container">
						<div class="col-md-12">
							<div class="preference">
								<h2>Welcome Back!</h2>
								<p>To keep connected with your friends please login with your personal info.</p>
								<a href="login" class="btn button">Sign In</a>
							</div>
						</div>
					</div>
				</div>
				<!-- End of Sidebar -->
			</div> <!-- Layout -->
		</main>
		<!-- Bootstrap core JavaScript
		================================================== -->
		<!-- Placed at the end of the document so the pages load faster -->
		<script src="${path}/js/jquery-3.3.1.min.js"></script>
		<script src="${path}/js/vendor/popper.min.js"></script>
		<script src="${path}/js/bootstrap.min.js"></script>
		<script src="${path}/chatify-js/signup.js"></script>
	</body>


</html>