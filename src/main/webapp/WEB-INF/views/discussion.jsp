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
      <%--<c:forEach items="${chats}" var="chat">
         <c:choose>
            <c:when test="${chat.type == 'friendRequest'}">
               <a href="#page-${chat.senderId}" class="filterDiscussions all unread single" data-toggle="list">
                  <img class="avatar-md" src="${chat.senderProfileImage}" data-toggle="tooltip" data-placement="top" title="${chat.senderFirstName}" alt="avatar">
                  <div class="status">
                     <i class="material-icons offline">fiber_manual_record</i>
                  </div>
                  <div class="new bg-gray">
                     <span>?</span>
                  </div>
                  <div class="data">
                     <h5>${chat.senderFirstName} ${chat.senderLastName}</h5>
                     <span>Feb 10</span>
                     <p>${chat.message}</p>
                  </div>
               </a>
            </c:when>
         </c:choose>
      </c:forEach>--%>
   </div>
</div>
