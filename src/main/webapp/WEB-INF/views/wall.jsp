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
		<!-- Custom CSS -->
		<link href="${path}/css/custom.css" type="text/css" rel="stylesheet">
		<!-- Favicon -->
		<link href="${path}/image/favicon.png" type="image/png" rel="icon">
	</head>
	<script type="text/javascript">
        var sessionFirstName = '${user.firstName}';
        var sessionLastName = '${user.lastName}';
        var sessionUserId = '${user.userId}';
        var sessionUserName = '${user.username}';
        var sessionProfileImage = '${user.profileImage}';
        var sessionPath = '${path}'
    </script>
	<body>
		<main>
			<div class="layout">

				<!-- Start of Navigation -->
				<div class="navigation">
                   <div class="container">
                      <div class="inside">
                         <div class="nav nav-tab menu">
                            <button class="btn"><img class="avatar-xl" src="${user.profileImage}" alt="avatar"></button>
                            <a href="#members" data-toggle="tab"><i class="material-icons">account_circle</i></a>
                            <a href="#discussions" data-toggle="tab" class="active"><i class="material-icons active">chat_bubble_outline</i></a>
                            <a href="#notifications" data-toggle="tab" class="f-grow1"><i class="material-icons">notifications_none</i></a>
                            <button class="btn mode"><i class="material-icons">brightness_2</i></button>
                            <a href="#settings" data-toggle="tab"><i class="material-icons">settings</i></a>
                            <button class="btn power" onclick="window.location.href='${path}/logout'"><i class="material-icons">power_settings_new</i></button>
                         </div>
                      </div>
                   </div>
                </div>
				<!-- End of Navigation -->

				<!-- Start of Sidebar -->
				<div class="sidebar" id="sidebar">
					<div class="container">
						<div class="col-md-12">
							<div class="tab-content">

								<!-- Start of Contacts -->
								<div class="tab-pane fade" id="members">
                                   <div class="search">
                                      <form class="form-inline position-relative">
                                         <input type="search" class="form-control" id="people" placeholder="Search for people...">
                                         <button type="button" class="btn btn-link loop"><i class="material-icons">search</i></button>
                                      </form>
                                      <button class="btn create" data-toggle="modal" data-target="#exampleModalCenter"><i class="material-icons">person_add</i></button>
                                   </div>
                                   <div class="list-group sort">
                                      <button class="btn filterMembersBtn active show" data-toggle="list" data-filter="all">All</button>
                                      <button class="btn filterMembersBtn" data-toggle="list" data-filter="online">Online</button>
                                      <button class="btn filterMembersBtn" data-toggle="list" data-filter="offline">Offline</button>
                                   </div>
                                   <div class="contacts">
                                      <h1>Contacts</h1>
                                      <div class="list-group" id="contacts" role="tablist">
                                         <c:forEach items="${contacts}" var="contact">
                                            <a href="javascript:void(0)" id="contact-${contact.contactId}"
                                                class="filterMembers all ${contact.isOnline ? 'online' : 'offline'} contact">
                                               <img class="avatar-md" src="${contact.profileImage}" data-toggle="tooltip"
                                                  data-placement="top" title="${contact.firstName}" alt="avatar">
                                               <div class="status">
                                                  <i class="material-icons ${contact.isOnline ? 'online' : 'offline'}">fiber_manual_record</i>
                                               </div>
                                               <div class="data">
                                                  <h5>${contact.firstName} ${contact.lastName}</h5>
                                                  <p>London, United Kingdom</p>
                                               </div>
                                               <div class="person-add">
                                                  <i class="material-icons">person</i>
                                               </div>
                                            </a>
                                         </c:forEach>
                                      </div>
                                   </div>
                                </div>
								<!-- End of Contacts -->

								<!-- Start of Discussions -->
								<jsp:include page="discussion.jsp" />
								<!-- End of Discussions -->

								<!-- Start of Notifications -->
								<jsp:include page="notification.jsp" />
								<!-- End of Notifications -->

								<!-- Start of Settings -->
								<div class="tab-pane fade" id="settings">
                                    <jsp:include page="setting.jsp" />
								</div>
								<!-- End of Settings -->
							</div>
						</div>
					</div>
				</div>
				<!-- End of Sidebar -->
				<!-- Start of Add Friends -->
				<div class="modal fade" id="exampleModalCenter" tabindex="-1" role="dialog" aria-hidden="true">
					<div class="modal-dialog modal-dialog-centered" role="document">
						<div class="requests">
							<div class="title">
								<h1>Add your friends</h1>
								<button type="button" class="btn" id="close-friend-req-btn" data-dismiss="modal" aria-label="Close"><i class="material-icons">close</i></button>
							</div>
							<div class="content">
									<div class="form-group">
                                       <label for="user">Username:</label>
                                       <div style="position: relative;">
                                          <input type="text" class="form-control" id="user" placeholder="Add recipient..." required>
                                          <div id="searched-user">
                                          </div>
                                       </div>
                                       <div id='searched-icon'>
                                       </div>
                                    </div>
									<div class="form-group">
										<label for="welcome">Message:</label>
										<textarea class="text-control" id="welcome" placeholder="Send your welcome message..."></textarea>
									</div>
									<button type="button" class="btn button w-100" id="send-frnd-req-btn">Send Friend Request</button>
									<div>
                                        <p id="friend-req-submit-error">&nbsp;</p>
                                    </div>
							</div>
						</div>
					</div>
				</div>
				<!-- End of Add Friends -->
				<!-- Start of Create Chat -->
				<div class="modal fade" id="startnewchat" tabindex="-1" role="dialog" aria-hidden="true">
					<div class="modal-dialog modal-dialog-centered" role="document">
						<div class="requests">
							<div class="title">
								<h1>Start new chat</h1>
								<button type="button" class="btn" data-dismiss="modal" aria-label="Close"><i class="material-icons">close</i></button>
							</div>
							<div class="content">
								<form>
									<div class="form-group">
										<label for="participant">Recipient:</label>
										<input type="text" class="form-control" id="participant" placeholder="Add recipient..." required>
										<div class="user" id="recipient">
											<img class="avatar-sm" src="${path}/image/avatars/avatar-female-5.jpg" alt="avatar">
											<h5>Keith Morris</h5>
											<button class="btn"><i class="material-icons">close</i></button>
										</div>
									</div>
									<div class="form-group">
										<label for="topic">Topic:</label>
										<input type="text" class="form-control" id="topic" placeholder="What's the topic?" required>
									</div>
									<div class="form-group">
										<label for="message">Message:</label>
										<textarea class="text-control" id="message" placeholder="Send your welcome message...">Hmm, are you friendly?</textarea>
									</div>
									<button type="submit" class="btn button w-100">Start New Chat</button>
								</form>
							</div>
						</div>
					</div>
				</div>
				<!-- End of Create Chat -->
				<div class="main">
                   <div class="tab-content" id="nav-tabContent">
                   </div>
				</div>
			</div> <!-- Layout -->
		</main>
		<!-- Bootstrap/Swipe core JavaScript
		================================================== -->
		<!-- Placed at the end of the document so the pages load faster -->
		<script src="${path}/js/jquery-3.3.1.min.js"></script>
		<script src="${path}/js/vendor/popper.min.js"></script>
		<script src="${path}/js/swipe.min.js"></script>
		<script src="${path}/js/bootstrap.min.js"></script>
		<script src="${path}/chatify-js/socket.js"></script>
		<script src="${path}/chatify-js/friend-request-common.js"></script>
		<script src="${path}/chatify-js/chat-common.js"></script>
		<script src="${path}/chatify-js/friend-request-fromserver.js"></script>
		<script src="${path}/chatify-js/friend-request-toserver.js"></script>
        <script src="${path}/chatify-js/chat-fromserver.js"></script>
        <script src="${path}/chatify-js/chat-toserver.js"></script>
		<script>
            $(document).ready(function () {
                $('[data-toggle="tooltip"]').tooltip();

                $(document).on('mouseenter', '[data-toggle="tooltip"]', function () {
                    if (!$(this).data('bs.tooltip')) {
                        $(this).tooltip();
                        $(this).tooltip('show');
                    }
                });
            });
        </script>
	</body>

</html>