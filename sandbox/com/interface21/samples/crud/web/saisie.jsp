<%@ include file="/WEB-INF/vues/jsp/commun/includes.jsp" %>
<h1><fmt:message key="admin.sirene.saisie.titre"/></h1>
<p class="error"><i21:bind path="command"><c:out value="${status.errorMessage}"/></i21:bind></p>
<fmt:message key="lnk.admin.sirene" var="lnk"/>
<form method="POST" action="<c:url value="/${lnk}"/>">
  <jsp:include page="champs/pays.jsp"/>
  <jsp:include page="champs/nom.jsp"/>
  <jsp:include page="champs/desc.jsp"/>
  <jsp:include page="champs/tel.jsp"/>
  <jsp:include page="champs/fax.jsp"/>
  <jsp:include page="champs/email.jsp"/>
  <p class="buttons">
  <c:if test="${command.id == 0}">
    <input type="submit" name="_insert" value="<fmt:message key="gen.creer"/>">
  </c:if>
  <c:if test="${command.id != 0}">
    <input type="submit" name="_update" value="<fmt:message key="gen.valider"/>">
  </c:if>
  <input type="submit" name="_remove" value="<fmt:message key="gen.supprimer"/>">
  <input type="submit" name="_cancel" value="<fmt:message key="gen.annuler"/>">
  </p>
</form>

