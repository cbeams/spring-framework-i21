<%@ include file="/WEB-INF/vues/jsp/commun/includes.jsp" %>
<p>
  <i21:bind path="command.fax">
    <span class="error"><c:out value="${status.errorMessage}"/></span>
	<p class="label"><fmt:message key="champ.fax"/></p>
	<input type="text" size="20" maxlength="32" name="fax" value="<c:out value="${status.value}"/>">
  </i21:bind>
</p>
