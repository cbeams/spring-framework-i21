-- Spring PetClinic demo --
@author Ken Krebs

This directory contains the web app source.
For deployment, it needs to be built with Apache Ant.
The only requirements are JDK >=1.3 and Ant >=1.5.

Run "ant" in this directory for available targets
(e.g. "ant build", "ant warfile"). Note that to start
Ant this way, you'll need an XML parser in your
classpath (e.g. jre/lib/ext; included in JDK 1.4).
You can also invoke Ant manually, in this directory.

Note that to be able to execute the web app with its
default settings, you'll need to start the HSQLDB
instance in db/hsqldb first, using server.bat.

Note 2: If you are using JBoss, the above note doesn't apply.
You must instead have copied the as/jboss/3.X.X xml files
in the deploy dir and JBoss must be running while you 
call the Ant with target all or test.

General note for JBoss users:
- in web.xml: comment the the log4j listener
- in build.properties: uncomment the jboss.root property
- in build.properties: set the port number for HSql to 1701
- if using JBoss 3.2.x, copy the files db/jboss/3.2.x/*.xml in your deploy directory
- For time being no database descriptor is provided for other JBoss versions, you have to write them
 
