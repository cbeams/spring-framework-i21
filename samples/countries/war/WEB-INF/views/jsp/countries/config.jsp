<%@ include file="../common/includes.jsp" %>

<h2><fmt:message key="config.title"/></h2>
<br/>
<p>You can use this demonstration in three different configurations:</p>
<h3>Presentation</h3>
<h4>1. Configuration <strong>simple</strong></h4>
<p>The list of the countries is generated in memory.</p>
<p>No data base is used.</p>
<p>It is the configuration provided at the beginning.</p>
<h4>2. Configuration <strong>copy</strong></h4>
<p>The list of the countries is generated in memory.</p>
<p>The data base is used only to be populated by the list in memory.</p>
<p>The application detects this configuration automatically and proposes a choice <strong>copy</strong> from the home page.</p>
<h4>3. Configuration <strong>data base</strong></h4>
<p>The list of the countries is read from the data base.</p>
<p>You should have tested the <strong>copy</strong> configuration and used successfully the copy in the data base function to be able to use this configuration. 
</p>
<br/>
<h3>Technic</h3>
<h4>1. Configuration <strong>simple</strong></h4>
<p>There is nothing to modify, it is the provided configuration.</p>
<h4>2. Configuration <strong>copy</strong></h4>
<p>In <strong>countries-servlet.xml</strong>, comment on part <strong>ONLY MEMORY OR ONLY DATABASE IMPLEMENTATION</strong>. Uncomment on part <strong>MEMORY+DATABASE IMPLEMENTATION FOR COPYING FROM MEMORY TO DATABASE</strong>.</p>
<p>In <strong>applicationContext.xml</strong>, comment on part <strong>In memory only version</strong>. Uncomment on part <strong>In memory + Database version for copying</strong></p>
<h4>3. Configuration <strong>data base</strong></h4>
<p>In <strong>countries-servlet.xml</strong>, comment on part <strong>MEMORY+DATABASE IMPLEMENTATION FOR COPYING FROM MEMORY TO DATABASE</strong>. Uncomment on part <strong>ONLY MEMORY OR ONLY DATABASE IMPLEMENTATION</strong>. You thus returned to the starting situation.</p>
<p>In <strong>applicationContext.xml</strong>, comment on part <strong>In memory + Database version for copying</strong>. Uncomment on part <strong>Database only version</strong>.</p>
<br/>
