<%@ include file="../../common/includes.jsp" %>

<ul>
  <fmt:message key="lnk.home" var="lnk"/>
  <li><a href="<c:url value="/${lnk}"/>"><fmt:message key="nav.home"/><span><fmt:message key="nav.home.info"/></span></a></li>
  <fmt:message key="lnk.countries.main" var="lnk"/>
  <li><a href="<c:url value="/${lnk}"/>"><fmt:message key="nav.countries.main"/><span><fmt:message key="nav.countries.main.info"/></span></a></li>
  <fmt:message key="lnk.countries.pdf" var="lnk"/>
  <li><a href="<c:url value="/${lnk}"/>" target="_blank"><fmt:message key="nav.countries.pdf"/><span><fmt:message key="nav.countries.pdf.info"/></span></a></li>
  <fmt:message key="lnk.countries.excel" var="lnk"/>
  <li><a href="<c:url value="/${lnk}"/>" target="_blank"><fmt:message key="nav.countries.excel"/><span><fmt:message key="nav.countries.excel.info"/></span></a></li>
  <c:import url="../common/endnav.jsp"/>
</ul>
