<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<P>
<H2>Veterinarians:</H2>
<TABLE border="true">
  <TH>Name</TH><TH>Specialties</TH>
  <c:forEach var="vetEntry" items="${vets}">
    <TR>
      <TD><c:out value="${vetEntry.value.firstName}"/> <c:out value="${vetEntry.value.lastName}"/></TD>
      <TD>
          <c:forEach var="specialty" items="${vetEntry.value.specialties}">
            <c:out value="${specialty}"/>
          </c:forEach>
      </TD>
    </TR>
  </c:forEach>
</TABLE>
<P>
<BR>
<A href="<c:url value="welcome.htm"/>">Home</A>
