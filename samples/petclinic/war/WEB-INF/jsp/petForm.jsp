<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<P>
<H2><c:if test="${command.id == 0}">New </c:if>Pet</H2>
<i21:bind path="command">
  <FONT color="red">
    <B><c:out value="${status.errorMessage}"/></B>
  </FONT>
</i21:bind>
<P>
<B>Owner:</B> <c:out value="${command.owner.firstName}"/> <c:out value="${command.owner.lastName}"/>
<P>
<FORM method="POST">
  <B>Name:</B>
  <i21:bind path="command.name">
    <FONT color="red">
      <B><c:out value="${status.errorMessage}"/></B>
    </FONT>
    <BR><INPUT type="text" maxlength="30" size="30" name="name" value="<c:out value="${status.value}"/>" >
  </i21:bind>
  <P>
  <B>Birth Date:</B>
  <i21:bind path="command.birthDate">
    <FONT color="red">
      <B><c:out value="${status.errorMessage}"/></B>
    </FONT>
    <BR><INPUT type="text" maxlength="10" size="10" name="birthDate" value="<c:out value="${status.value}"/>" />
  </i21:bind>
  <BR>(yyyy-mm-dd)
  <P>
  <B>Type:</B>
  <i21:bind path="command.typeId">
    <FONT color="red">
      <B><c:out value="${status.errorMessage}"/></B>
    </FONT>
    <BR>
    <SELECT name="typeId">
      <c:forEach var="typeEntry" items="${types}">
        <c:if test="${command.typeId == typeEntry.key}">
          <OPTION selected="<c:out value="${command.typeId}"/>" value="<c:out value="${typeEntry.key}"/>"><c:out value="${typeEntry.value.name}"/></OPTION>
        </c:if>
        <c:if test="${command.typeId != typeEntry.key}">
          <OPTION value="<c:out value="${typeEntry.key}"/>"><c:out value="${typeEntry.value.name}"/></OPTION>
        </c:if>
      </c:forEach>
    </SELECT>
  </i21:bind>
  <P>
  <INPUT type="hidden" name="ownerId" value="<c:out value="${command.owner.id}"/>"/>
  <c:if test="${command.id == 0}">
    <INPUT type = "submit" value="Add Pet"  />
  </c:if>
  <c:if test="${command.id != 0}">
    <INPUT type = "submit" value="Update Pet"  />
  </c:if>
</FORM>
<P>
<BR>
<A href="<c:url value="welcome.htm"/>">Home</A>
