<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<P>
<H2>Find Owners:</H2>
<i21:bind path="command">
  <FONT color="red">
    <B><c:out value="${status.errorMessage}"/></B>
  </FONT>
</i21:bind>
<P>
<FORM method="POST">
  <jsp:include page="/WEB-INF/jsp/fields/lastName.jsp"/>
  <INPUT type = "submit" value="Find Owners"  />
</FORM>
<P>
<BR>
<A href="<c:url value="addOwner.htm"/>">Add Owner</A>
<P>
<BR>
<A href="<c:url value="welcome.htm"/>">Home</A>
