<%@ include file="../common/includes.jsp" %>

<h2><fmt:message key="home.title"/></h2>
<p>&nbsp;</p>
<h3><fmt:message key="app.name"/></h3>
<p class="version">version <strong><fmt:message key="app.version"/></strong></p>
<fmt:message key="lnk.sf" var="sf"/>
<div class="news"><fmt:message key="app.desc"><fmt:param value="${sf}"/></fmt:message></div>
<fmt:message key="lnk.sources" var="src"/>
<div class="info"><fmt:message key="home.sources"><fmt:param value="${src}"/></fmt:message></div>
