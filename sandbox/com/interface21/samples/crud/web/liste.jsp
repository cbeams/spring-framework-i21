<%@ include file="/WEB-INF/vues/jsp/commun/includes.jsp" %>
<h1><fmt:message key="admin.sirene.liste.titre"/></h1>
<script language="javascript">
  function go(id) {
    var url = "<c:url value=""/>?_edit=1&id=" + id;
    window.location = url;
  }
</script>
<c:if test="${not empty message}"><p class="msg"><fmt:message key="${message}"/></p></c:if>
<table>
  <tr class="title">
    <th><a href="<c:url value=""><c:param name="sort.property" value="pays.name"/></c:url>"><fmt:message key="col.pays"/></a></th>
    <th><a href="<c:url value=""><c:param name="sort.property" value="nom"/></c:url>"><fmt:message key="col.nom"/></a></th>
    <th><fmt:message key="col.tel"/></th>
    <th><fmt:message key="col.fax"/></th>
    <th><fmt:message key="col.email"/></th>
  </tr>
  <c:forEach items="${sirenes.pageList}" var="sir" varStatus="s">
    <tr <c:if test="${s.count % 2 == 0}">class="odd"</c:if>
      onclick="go('<c:out value="${sir.id}"/>')" 
      onmouseover="this.style.cursor = 'hand';this.style.backgroundColor = '#dfdfff'" 
      onmouseout="this.style.backgroundColor = ''">
      <td><c:out value="${sir.pays.name}"/></td>
      <td><c:out value="${sir.nom}"/></td>
      <td><c:out value="${sir.tel}"/></td>
      <td><c:out value="${sir.fax}"/></td>
      <td><c:out value="${sir.email}"/></td>
    </tr>
  </c:forEach>
</table>
<hr/>
<p class="buttons"><a href="<c:url value=""><c:param name="_edit" value=""/></c:url>"><fmt:message key="gen.nouveau"/></a></p>