<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<B>First Name:</B>
<spring:bind path="command.firstName">
  <FONT color="red">
    <B><c:out value="${status.errorMessage}"/></B>
  </FONT>
  <BR><INPUT type="text" maxlength="30" size="30" name="firstName" value="<c:out value="${status.value}"/>" >
</spring:bind>
<P>
