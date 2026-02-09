<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="path" value="${pageContext.servletContext.contextPath}"></c:set>
<div id="discussions" class="tab-pane fade active show">
   <div class="search">
      <form class="form-inline position-relative">
         <input type="search" class="form-control" id="conversations" placeholder="Search for conversations...">
         <button type="button" class="btn btn-link loop"><i class="material-icons">search</i></button>
      </form>
      <button class="btn create" data-toggle="modal" data-target="#startnewchat"><i class="material-icons">create</i></button>
   </div>
   <div class="list-group sort">
      <button class="btn filterDiscussionsBtn active show" data-toggle="list" data-filter="all">All</button>
      <button class="btn filterDiscussionsBtn" data-toggle="list" data-filter="read">Read</button>
      <button class="btn filterDiscussionsBtn" data-toggle="list" data-filter="unread">Unread</button>
   </div>
   <div class="discussions">
      <h1>Chats</h1>
      <div class="list-group" id="chats" role="tablist">
         <c:forEach items="${chatGroups}" var="chatGroup">
            <a id="chatgroup-${chatGroup.contact.userId}" href="#" class="filterDiscussions all unread single">
               <img class="avatar-md" src="${chatGroup.contact.profileImage}" data-toggle="tooltip"
                  data-placement="top" title="${chatGroup.contact.firstName}" alt="avatar">
               <div class="status">
                  <i class="material-icons ${chatGroup.contact.isOnline ? 'online' : 'offline'}">fiber_manual_record</i>
               </div>
               <div class="new bg-gray">
                  <span>?</span>
               </div>
               <div class="data">
                  <h5>${chatGroup.contact.firstName} ${chatGroup.contact.lastName}</h5>
                  <span>${chatGroup.chats[0].formattedDate}</span>
                  <p id="p-${chatGroup.contact.userId}">
                     ${chatGroup.chats[0].message}
                     <c:if test="${not empty chatGroup.chats
                                  and chatGroup.chats[0].type eq 'friendRequest'
                                  and sessionScope.userId eq chatGroup.chats[0].senderId}">
                         ${chatGroup.contact.firstName} ${chatGroup.contact.lastName}
                     </c:if>
                  </p>
               </div>
            </a>
         </c:forEach>
      </div>
   </div>
</div>
