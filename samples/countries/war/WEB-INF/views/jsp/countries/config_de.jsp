<%@ include file="../common/includes.jsp" %>

<h2><fmt:message key="config.title"/></h2>
<br />
<p>Sie können dieses demo in drei anderen Konfigurationen benutzen:</p>
<h3>Vorstellung</h3>
<h4>1. Konfiguration <strong>Einfach</strong></h4>
<p>Die Liste der Länder wird in Gedächtnis erzeugt.</p>
<p>Keine Datenbank wird benutzt.</p>
<p>Es ist die dem Anfang gelieferte Konfiguration.</p>
<h4>2. Konfiguration <strong>Kopie</strong></h4>
<p>Die Liste der Länder wird in Gedächtnis erzeugt.</p>
<p>Die Datenbank wird nicht benutzt, daß, um durch die Liste in Gedächtnis bevölkert zu werden.</p>
<p>Die Anwendung stellt automatisch diese Konfiguration fest und schlägt eine Wahl vor <strong>Kopie</strong> auf der Empfangsseite.</p>
<h4>3. Konfiguration <strong>Datenbank </strong></h4>
<p>Die Liste der Länder wird seit der Datenbank gelesen.</p>
<p>Man muß die <strong>Kopie</strong> Konfiguration getestet haben und erfolgreich benutzt die Kopiefunktion in der Datenbank, um diese Konfiguration benutzen zu können.</p>
<br />
<h3>Technik</h3>
<h4>1. Configuration <strong>Einfach</strong></h4>
<p>Es gibt nichts zu ändern, es ist die gelieferte Konfiguration.</p>
<h4>2. Configuration <strong>copy</strong></h4>
<p>In <strong>countries-servlet.xml</strong>, kommentieren den Teil <strong>ONLY MEMORY OR ONLY DATABASE IMPLEMENTATION</strong>. Abkommentieren den Teil <strong>MEMORY+DATABASE IMPLEMENTATION FOR COPYING FROM MEMORY TO DATABASE</strong>.</p>
<p>In <strong>applicationContext.xml</strong>, kommentieren den Teil <strong>In memory only version</strong>. Abkommentieren den Teil <strong>In memory + Database version for copying</strong></p>
<h4>3. Configuration <strong>data base</strong></h4>
<p>In <strong>countries-servlet.xml</strong>, kommentieren den Teil <strong>MEMORY+DATABASE IMPLEMENTATION FOR COPYING FROM MEMORY TO DATABASE</strong>. Abkommentieren den Teil <strong>ONLY MEMORY OR ONLY DATABASE IMPLEMENTATION</strong>. Sie sind so zur Ausgangslage zurückgekommen.</p>
<p>In <strong>applicationContext.xml</strong>, kommentieren den Teil <strong>In memory + Database version for copying</strong>. Abkommentieren den Teil <strong>Database only version</strong>.</p>
<br />
