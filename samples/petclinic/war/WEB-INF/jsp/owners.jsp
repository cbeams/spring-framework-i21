<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<P>
<H2>Owners:</H2>
<TABLE border="true">
  <TH>Name</TH><TH>Address</TH><TH>City</TH><TH>Telephone</TH><TH>Pets</TH>
  <c:forEach var="owner" items="${selections}">
    <TR>
      <TD>
        <FORM method=POST action="<c:url value="owner.htm"/>">
          <INPUT type="hidden" name="ownerId" value="<c:out value="${owner.id}"/>"/>
          <INPUT type="submit" value="<c:out value="${owner.firstName}"/> <c:out value="${owner.lastName}"/>"/>
        </FORM>
      </TD>
      <TD><c:out value="${owner.address}"/></TD>
      <TD><c:out value="${owner.city}"/></TD>
      <TD><c:out value="${owner.telephone}"/></TD>
      <TD>
        <c:forEach var="pet" items="${owner.pets}">
          <c:out value="${pet.name}"/>
        </c:forEach>
      </TD>
    </TR>
  </c:forEach>
</TABLE>
<P>
<BR>
<A href="<c:url value="welcome.htm"/>">Home</A>
