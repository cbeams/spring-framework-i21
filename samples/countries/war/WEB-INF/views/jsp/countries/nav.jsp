<%@ include file="../common/includes.jsp" %>

<ul>
  <fmt:message key="lnk.home" var="lnk"/>
  <li><a href="<c:url value="/${lnk}"/>"><fmt:message key="nav.home"/><span><fmt:message key="nav.home.info"/></span></a></li>
  <fmt:message key="lnk.countries.main" var="lnk"/>
  <li><a href="<c:url value="/${lnk}"/>"><fmt:message key="nav.countries.main"/><span><fmt:message key="nav.countries.main.info"/></span></a></li>
  <fmt:message key="lnk.config" var="lnk"/>
  <li><a href="<c:url value="/${lnk}"/>"><fmt:message key="nav.config"/><span><fmt:message key="nav.config.info"/></span></a></li>
  <c:if test="${copyAvailable}">
    <fmt:message key="lnk.copy" var="lnk"/>
    <li><a href="<c:url value="/${lnk}"/>"><fmt:message key="nav.copy"/><span><fmt:message key="nav.copy.info"/></span></a></li>
  </c:if>
  <fmt:message key="lnk.javadoc" var="lnk"/>
  <li><a href="<c:url value="/${lnk}"/>" target="_blank"><fmt:message key="nav.javadoc"/><span><fmt:message key="nav.javadoc.info"/></span></a></li>
  <c:import url="../common/endnav.jsp"/>
</ul>