<%@ include file="/WEB-INF/vues/jsp/commun/includes.jsp" %>
<p>
  <i21:bind path="command.email">
    <span class="error"><c:out value="${status.errorMessage}"/></span>
	<p class="label"><fmt:message key="champ.email"/></p>
	<input type="text" size="32" maxlength="64" name="email" value="<c:out value="${status.value}"/>">
  </i21:bind>
</p>
