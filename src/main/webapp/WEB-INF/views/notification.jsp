<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="path" value="${pageContext.servletContext.contextPath}"></c:set>
<div id="notifications" class="tab-pane fade">
   <div class="search">
      <form class="form-inline position-relative">
         <input type="search" class="form-control" id="notice" placeholder="Filter notifications...">
         <button type="button" class="btn btn-link loop"><i class="material-icons filter-list">filter_list</i></button>
      </form>
   </div>
   <div class="list-group sort">
      <button class="btn filterNotificationsBtn active show" data-toggle="list" data-filter="all">All</button>
      <button class="btn filterNotificationsBtn" data-toggle="list" data-filter="latest">Latest</button>
      <button class="btn filterNotificationsBtn" data-toggle="list" data-filter="oldest">Oldest</button>
   </div>
   <div class="notifications">
      <h1>Notification</h1>
      <div class="list-group" id="alerts" role="tablist">
            <c:forEach items="${notifications}" var="noti">
            <a href="#" class="filterNotifications all ${noti.isRecent ? 'latest' : 'oldest'} notification" data-toggle="list">
               <img class="avatar-md" src="${noti.senderProfileImage}" data-toggle="tooltip" data-placement="top" title="${noti.senderFirstName}" alt="avatar">
               <div class="status">
                  <i class="material-icons online">fiber_manual_record</i>
               </div>
               <div class="data">
                  <p>${noti.message}</p>
                  <span>${noti.formattedDate}</span>
               </div>
            </a>
         </c:forEach>
      </div>
   </div>
</div>
