<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<P>
<H2><c:if test="${command.id == 0}">New </c:if>Visit:</H2>
<i21:bind path="command">
  <FONT color="red">
    <B><c:out value="${status.errorMessage}"/></B>
  </FONT>
</i21:bind>
<B>Pet:</B>
<TABLE border="true">
  <TH>Name</TH><TH>Birth Date</TH><TH>Type</TH><TH>Owner</TH>
  <TR>
    <TD><c:out value="${command.pet.name}"/></TD>
    <TD><fmt:formatDate value="${command.pet.birthDate}" pattern="yyyy-MM-dd"/></TD>
    <TD><c:out value="${types[command.pet.typeId].name}"/></TD>
    <TD><c:out value="${command.pet.owner.firstName}"/> <c:out value="${command.pet.owner.lastName}"/></TD>
  </TR>
</TABLE>
<P>
<FORM method="POST">
  <B>Date:</B>
  <i21:bind path="command.visitDate">
    <FONT color="red">
      <B><c:out value="${status.errorMessage}"/></B>
    </FONT>
    <BR><INPUT type="text" maxlength="10" size="10" name="visitDate" value="<c:out value="${status.value}"/>" />
  </i21:bind>
  <BR>(yyyy-mm-dd)
  <P>
  <B>Description:</B>
  <i21:bind path="command.description">
    <FONT color="red">
      <B><c:out value="${status.errorMessage}"/></B>
    </FONT>
    <BR><TEXTAREA rows="10" cols="25" name="description" value="<c:out value="${status.value}"/>" ></TEXTAREA>
  </i21:bind>
  <P>
  <INPUT type="hidden" name="petId" value="<c:out value="${command.pet.id}"/>"/>
  <INPUT type = "submit" value="Add Visit"  />
</FORM>
<P>
<BR>
<B>Previous Visits:</B>
<TABLE border="true">
  <TH>Date</TH><TH>Description</TH>
  <c:forEach var="visit" items="${command.pet.visits}">
    <TR>
      <TD><fmt:formatDate value="${visit.visitDate}" pattern="yyyy-MM-dd"/></TD>
      <TD><c:out value="${visit.description}"/></TD>
    </TR>
  </c:forEach>
</TABLE>
<P>
<BR>
<A href="<c:url value="welcome.htm"/>">Home</A>
