<%@ include file="/WEB-INF/vues/jsp/commun/includes.jsp" %>
<p>
  <i21:bind path="command.description">
    <p class="error"><c:out value="${status.errorMessage}"/></p>
    <p class="label"><fmt:message key="champ.desc"/></p>
    <input type="text" size="32" maxlength="64" name="description" value="<c:out value="${status.value}"/>">
  </i21:bind>
</p>
