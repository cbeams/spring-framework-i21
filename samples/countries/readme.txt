-- Spring Countries demo --
@author Jean-Pierre Pawlak

This directory contains the web app source.
For deployment, it needs to be built with Apache Ant.
The only requirements are JDK >=1.3 and Ant >=1.5.

Run "ant" in this directory for available targets
(e.g. "ant build", "ant warfile"). Note that to start
Ant this way, you'll need an XML parser in your
classpath (e.g. jre/lib/ext; included in JDK 1.4).
You can also invoke Ant manually, in this directory.
For windows systems, you can alternatively use warfile.bat
instead of "ant warfile".

Your warfile will be cretad in the 'dist' directory.

The application is set for the first scenario, 
generating data in memory.
This will normally run as is in most application servers.

For the additional scenarios, you will find below 
the instructions for JBoss 3.2.x. 
For other servers you will to have to make yourself the 
database mapping between the server and the application
by bounding the JNDI locations as does the file 'jboss-web.xml'
for JBoss.

Passing to scenario 2
---------------------
This scenario will use the memoryDao like the first.
But, in addition, it will set a secondary databaseDao.
So, you will be able, using the application, to read the data from
the memoryDao and write into the databaseDao.
The main goal of this scenario is to put the countries data in your database.

Two databases have been tested HSql and MySql.
Switching from one to the other is only a matter of mapping the right database
to the application known JNDI location.
For JBoss, this is done in 'jboss-web.xml'. This file can be found in sa/jboss
and has to be copied under war/WEB-INF. Inside the file uncomment the setting 
for the database of your choice and comment the other.

Jboss must be able to drive the connections. For this, you will:
1) copy the mySql driver from jdbc directory to <JBOSS_HOME>/default/lib 
(if you use the default configuration, obviously). The hsql driver should 
be there, so you don't need to copy it. 
2) copy the database services in the JBoss deploy directory. For Jboss3.2.x,
you will find the two files under/sa/jboss/3.2.x.

Now that the server is set up, we will change the application configuration.
Just two little changes have to be made.
1) in war/WEB-INF/applicationContext.xml: comment the scenario 1 and uncomment the 2.
You are so just defining a "secondDaoCountry" bean and its "dataSource" bean.
2) in war/WEB-INF/countries-servlet.xml: uncomment the property "secondDaoCountry"
near the end of the file.
3) Obviously, you will stick with the default 'J2EE' Jdbc definition.
4) In jboss-service.xml, for avoiding "MessageCache" error logs, set
	<attribute name="RecursiveSearch">True</attribute>

Now that all is set, rebuild the war file with the ant command and deploy the 
new generated war file.

Test the application, and go to the new choice "Copy". A message tell you about 
the work. If you see that the data were not copied, you will have to check the logs.

Passing to scenario 3
---------------------
This is the ultimate goal. You come back to using only one DAO, but you use the
database one instead the memory one as in the first scenario.
1) in war/WEB-INF/applicationContext.xml: comment the scenario 2 and uncomment the 3.
You just pick up the configuration of your previously "secondDaoCountry" to put 
them as primary DAO. You don't need anymore a second Dao.
2) in war/WEB-INF/countries-servlet.xml: re-comment the property "secondDaoCountry"
near the end of the file as you don't have any.

Now, once more, rebuild the war file with the ant command and deploy the 
new generated war file.

You don't have the 'copy' choice, because the application checks the configuration 
and allow this only when a secondDao of 'DATABASE' type is detected.

And then ?
----------
It rest to look at the sources and configuration to study and understand what you
made and how. 
This demo uses just a few Jdbc capabilities of Spring, mainly to demonstrate 
the simple switching between two DAOs implementing the same interface.
For the rest, the focus is on the web part of the framework. Nevertheless, the
current demo doesn't show the user input nor the validation process.

Additional note
---------------
Depending on your setting preferences, you have two strategies introduced:

- In Petclinic:
You define both databases in web.xml, jboss-web.xml (app. server binding) and
in the app. server itself.
Switching between databases is done in Spring 'applicationContext.xml' file.

- In Countries:
On the application side, only one configuration is known. The switching is done 
in the binding with the app. server (jboss-web.xml in the case of Jboss).




