<%@ include file="/WEB-INF/vues/jsp/commun/includes.jsp" %>
<p>
  <i21:bind path="command.tel">
    <span class="error"><c:out value="${status.errorMessage}"/></span>
	<p class="label"><fmt:message key="champ.tel"/></p>
	<input type="text" size="20" maxlength="32" name="tel" value="<c:out value="${status.value}"/>">
  </i21:bind>
</p>
