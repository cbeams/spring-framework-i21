<%@ include file="/WEB-INF/vues/jsp/commun/includes.jsp" %>
<p>
  <i21:bind path="command.pays">
    <span class="error"><c:out value="${status.errorMessage}"/></span>
	<p class="label"><fmt:message key="champ.pays"/></p>
    <select name="pays">
        <c:forEach items="${sisPays}" var="pay"><option <c:if test="${status.value == pay.code}"> selected </c:if> value="<c:out value="${pay.code}"/>"><c:out value="${pay.name}"/></option></c:forEach>
    </select>
  </i21:bind>
</p>
