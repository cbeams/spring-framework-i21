<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<P>
<BR>
<TABLE border="true">
  <TH>
    <H3>Owner:</H3>        
    <FORM method=GET action="<c:url value="editOwner.htm"/>">
      <INPUT type="hidden" name="ownerId" value="<c:out value="${model.owner.id}"/>"/>
      <INPUT type="submit" value="Edit Owner"/>
    </FORM>
  </TH>
  <TR><TD>
    <TABLE border="true">
      <TH>Name</TH><TH>Address</TH><TH>City</TH><TH>Telephone</TH>
      <TR>
        <TD><c:out value="${model.owner.firstName}"/> <c:out value="${model.owner.lastName}"/></TD>
        <TD><c:out value="${model.owner.address}"/></TD>
        <TD><c:out value="${model.owner.city}"/></TD>
        <TD><c:out value="${model.owner.telephone}"/></TD>
      </TR>
    </TABLE>
  </TD></TR>
</TABLE>
<P>
<BR>
<c:forEach var="pet" items="${model.owner.pets}">
  <P>
  <TABLE border="true">
    <TH>
      <H3>Pet:</H3>
      <FORM method=GET action="<c:url value="editPet.htm"/>">
        <INPUT type="hidden" name="petId" value="<c:out value="${pet.id}"/>"/>
        <INPUT type="submit" value="Edit Pet"/>
      </FORM>
    </TH>
    <TR><TD>
      <TABLE border="true">
        <TH>Name</TH><TH>Birth Date</TH><TH>Type</TH>
          <TR>
            <TD><c:out value="${pet.name}"/></TD>
            <TD><fmt:formatDate value="${pet.birthDate}" pattern="yyyy-MM-dd"/></TD>
            <TD><c:out value="${model.types[pet.typeId].name}"/></TD>
          </TR>
      </TABLE>
    </TD></TR>
  </TABLE>
  <TABLE border="true">
    <TH>
      <H3>Visits:</H3>
      <FORM method=GET action="<c:url value="addVisit.htm"/>">
        <INPUT type="hidden" name="petId" value="<c:out value="${pet.id}"/>"/>
        <INPUT type="submit" value="Add Visit"/>
      </FORM>
    </TH>
    <TR><TD>
      <TABLE border="true">
        <TH> Date</TH><TH>Description</TH>
        <c:forEach var="visit" items="${pet.visits}">
          <TR>
            <TD><fmt:formatDate value="${visit.visitDate}" pattern="yyyy-MM-dd"/></TD>
            <TD><c:out value="${visit.description}"/></TD>
          </TR>
        </c:forEach>
      </TABLE>
    </TD></TR>
  </TABLE>
  <P>
  <BR>
</c:forEach>
<P>
<FORM method=GET action="<c:url value="addPet.htm"/>">
  <INPUT type="hidden" name="ownerId" value="<c:out value="${model.owner.id}"/>"/>
  <INPUT type="submit" value="Add Pet"/>
</FORM>
<P>
<BR>
<A href="<c:url value="welcome.htm"/>">Home</A>
  
