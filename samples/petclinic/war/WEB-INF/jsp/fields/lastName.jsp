<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<B>Last Name:</B>
<i21:bind path="command.lastName">
  <FONT color="red">
    <B><c:out value="${status.errorMessage}"/></B>
  </FONT>
  <BR><INPUT type="text" maxlength="30" size="30" name="lastName" value="<c:out value="${status.value}"/>" >
</i21:bind>
<P>
