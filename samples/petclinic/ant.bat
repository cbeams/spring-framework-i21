set CLASSPATH=%CLASSPATH%;..\..\lib\junit\junit.jar;lib\hsqldb.jar;lib\mysql-connector-java-2.0.14.jar
%JAVA_HOME%/bin/java -cp ../../lib/ant/ant.jar;../../lib/junit/junit.jar;%JAVA_HOME%/lib/tools.jar org.apache.tools.ant.Main %1
