<%@ include file="../common/includes.jsp" %>

<h2><fmt:message key="copy.title"/></h2>
<p>&nbsp;</p>
<c:choose>
  <c:when test="${copyMade}">
    <h3><fmt:message key="copy.ok"/></h3>
  </c:when>
  <c:otherwise>
    <h3><fmt:message key="copy.nok"/></h3>
  </c:otherwise>
</c:choose>
<br/>
<br/>
<br/>

