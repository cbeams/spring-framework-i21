<%@ include file="../common/includes.jsp" %>

<h2><fmt:message key="config.title"/></h2>
<br />
<p>Vous pouvez utiliser cette démo en trois configurations différentes:</p>
<h3>Présentation</h3>
<h4>1. Configuration <strong>simple</strong></h4>
<p>La liste des pays est générée en mémoire.</p>
<p>Aucune base de données n'est utilisée.</p>
<p>C'est la configuration fournie au départ.</p>
<h4>2. Configuration <strong>copie</strong></h4>
<p>La liste des pays est générée en mémoire.</p>
<p>La base de données n'est utilisée que pour être peuplée par la liste en mémoire.</p>
<p>L'application détecte automatiquement cette configuration et propose un choix <strong>copie</strong> sur la page d'accueil.</p>
<h4>3. Configuration <strong>base de données</strong></h4>
<p>La liste des pays est lue depuis la base de données.</p>
<p>Il faut avoir testé l'utilisation <strong>copie</strong> et utilisé avec succès la fonction de copie dans la base de données pour pouvoir utiliser cette configuration.</p>
<br />
<h3>Technique</h3>
<h4>1. Configuration <strong>simple</strong></h4>
<p>Il n'y a rien à modifier, c'est la configuration fournie.</p>
<h4>2. Configuration <strong>copie</strong></h4>
<p>Dans <strong>countries-servlet.xml</strong>, commentez la partie <strong>ONLY MEMORY OR ONLY DATABASE IMPLEMENTATION</strong>. Décommentez la partie <strong>MEMORY+DATABASE IMPLEMENTATION FOR COPYING FROM MEMORY TO DATABASE</strong>.</p>
<p>Dans <strong>applicationContext.xml</strong>, commentez la partie <strong>In memory only version</strong>. Décommentez la partie <strong>In memory + Database version for copying</strong></p>
<h4>3. Configuration <strong>base de données</strong></h4>
<p>Dans <strong>countries-servlet.xml</strong>, commentez la partie <strong>MEMORY+DATABASE IMPLEMENTATION FOR COPYING FROM MEMORY TO DATABASE</strong>. Décommentez la partie <strong>ONLY MEMORY OR ONLY DATABASE IMPLEMENTATION</strong>. Vous êtes ainsi revenus à la situation de départ.</p>
<p>Dans <strong>applicationContext.xml</strong>, commentez la partie <strong>In memory + Database version for copying</strong>. Décommentez la partie <strong>Database only version</strong>.</p>
<br />
