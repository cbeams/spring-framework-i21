<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<P>
<H2><c:if test="${command.id == 0}">New </c:if>Owner:</H2>
<i21:bind path="command">
  <FONT color="red">
    <B><c:out value="${status.errorMessage}"/></B>
  </FONT>
</i21:bind>
<P>
<FORM method="POST">
  <jsp:include page="/WEB-INF/jsp/fields/firstName.jsp"/>
  <jsp:include page="/WEB-INF/jsp/fields/lastName.jsp"/>
  <jsp:include page="/WEB-INF/jsp/fields/address.jsp"/>
  <jsp:include page="/WEB-INF/jsp/fields/city.jsp"/>
  <jsp:include page="/WEB-INF/jsp/fields/telephone.jsp"/>
  <c:if test="${command.id == 0}">
    <INPUT type = "submit" value="Add Owner"  />
  </c:if>
  <c:if test="${command.id != 0}">
    <INPUT type = "submit" value="Update Owner"  />
  </c:if>
</FORM>
<P>
<BR>
<A href="<c:url value="welcome.htm"/>">Home</A>
