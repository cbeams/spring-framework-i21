<%@ include file="../../common/includes.jsp" %>

<h2><fmt:message key="countries.detail.title"/></h2>
<p>&nbsp;</p>
<h3><c:out value="${country.name}"/></h3>
<strong><fmt:message key="code"/>:</strong> <c:out value="${country.code}"/><br>
<c:set var="linkimg"><spring:theme code="img-back"/></c:set>
<div align="center">
  <a href="javascript:history.go(-1)"><img src="<c:url value="${linkimg}"/>" alt="gen.back"/></a>
</div>
      