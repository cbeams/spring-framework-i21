<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@ include file="/WEB-INF/views/jsp/common/includes.jsp" %>

<html>
  <head>
    <title><fmt:message key="${htitle}"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <c:set var="css"><i21:theme code="css"/></c:set>
    <c:if test="${not empty css}"><link rel="stylesheet" href="<c:url value="${css}"/>" type="text/css"></c:if>
  </head>

  <body>
    <a name="top"><div id="nav"><c:import url="${nav}"/></div></a>
    <div id="top"><c:import url="top.jsp"/></div>
    <div id="content"><h1><fmt:message key="app.short"/></h1><c:import url="${content}"/></div>
    <div id="footer"><c:import url="${nav}"/></div>
  </body>
</html>

