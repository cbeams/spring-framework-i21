<%@ include file="/WEB-INF/vues/jsp/commun/includes.jsp" %>
<p>
  <i21:bind path="command.nom">
    <span class="error"><c:out value="${status.errorMessage}"/></span>
	<p class="label"><fmt:message key="champ.nom"/></p>
	<input type="text" size="32" maxlength="63" name="nom" value="<c:out value="${status.value}"/>">
  </i21:bind>
</p>
