<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<B>City:</B>
<spring:bind path="command.city">
  <FONT color="red">
    <B><c:out value="${status.errorMessage}"/></B>
  </FONT>
  <BR><INPUT type="text" maxlength="80" size="30" name="city" value="<c:out value="${status.value}"/>" >
</spring:bind>
<P>
