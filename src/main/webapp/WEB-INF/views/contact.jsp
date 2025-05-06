<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="path" value="${pageContext.servletContext.contextPath}"></c:set>

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
         <a href="#" class="filterMembers all online contact" data-toggle="list">
            <img class="avatar-md" src="${path}/image/avatars/avatar-female-1.jpg" data-toggle="tooltip" data-placement="top" title="Janette" alt="avatar">
            <div class="status">
               <i class="material-icons online">fiber_manual_record</i>
            </div>
            <div class="data">
               <h5>Janette Dalton</h5>
               <p>Sofia, Bulgaria</p>
            </div>
            <div class="person-add">
               <i class="material-icons">person</i>
            </div>
         </a>
         <a href="#" class="filterMembers all offline contact" data-toggle="list">
            <img class="avatar-md" src="${path}/image/avatars/avatar-male-3.jpg" data-toggle="tooltip" data-placement="top" title="Ryan" alt="avatar">
            <div class="status">
               <i class="material-icons offline">fiber_manual_record</i>
            </div>
            <div class="data">
               <h5>Ryan Foster</h5>
               <p>Oslo, Norway</p>
            </div>
            <div class="person-add">
               <i class="material-icons">person</i>
            </div>
         </a>
         <a href="#" class="filterMembers all offline contact" data-toggle="list">
            <img class="avatar-md" src="${path}/image/avatars/avatar-male-4.jpg" data-toggle="tooltip" data-placement="top" title="Mildred" alt="avatar">
            <div class="status">
               <i class="material-icons offline">fiber_manual_record</i>
            </div>
            <div class="data">
               <h5>Mildred Bennett</h5>
               <p>London, United Kingdom</p>
            </div>
            <div class="person-add">
               <i class="material-icons">person</i>
            </div>
         </a>
      </div>
   </div>
</div>
