THE SPRING FRAMEWORK, release 0.9.1 (August 2003)
-------------------------------------------------
http://www.springframework.org


1. INTRODUCTION

Spring is a J2EE application framework based on code published in "Expert One-on-One J2EE Design and Development" by Rod Johnson (Wrox, 2002).

Spring includes:
* Powerful JavaBeans-based configuration management, applying Inversion-of-Control principles. This makes wiring up applications quick and easy. No more singletons littered throughout your codebase; no more arbitrary properties file. One consistent and elegant approach everywhere.
* JDBC abstraction layer that offers a meaningful exception hierarchy (no more pulling vendor codes out of SQLException), simplifies error handling, and greatly reduces the amount of code you'll need to write. You'll never need to write another finally block to use JDBC again.
* Similar abstraction layer for transaction management, allowing for pluggable transaction managers, and making it easy to demarcate transactions without dealing with low-level issues. Strategies for JTA and a single JDBC DataSource are included.
* Integration with Hibernate and JDO, in terms of resource holders, DAO implementation support, and transaction strategies. First-class Hibernate support with lots of IoC convenience features, solving many typical Hibernate integration issues.
* AOP functionality, fully integrated into Spring configuration management. You can AOP-enable any object managed by Spring, adding aspects such as declarative transaction management. With Spring, you can have declarative transaction management without EJB... even without JTA, if you're using a single database in Tomcat or another web container without JTA support.
* Flexible MVC web application framework, built on core Spring functionality. This framework is highly configurable and accommodates multiple view technologies.

You can use all of Spring's functionality in any J2EE server, and most of it in non-managed environments too. A central focus of Spring is to allow for reusable business and data access objects that are not tied to specific J2EE services. Such objects can be used in J2EE environments with or without EJB, standalone applications, test environments, etc without any hassle.

Spring has a layered architecture. All its functionality builds on lower levels. So you can e.g. use the JavaBeans configuration management without using the MVC framework or AOP support. But if you use the MVC framework or AOP support, you'll find they build on the configuration framework, so you can apply your knowledge about it immediately.


2. RELEASE INFO

The Spring Framework is released under the terms of the Apache Software License (see license.txt). This is the second public release towards 1.0. 

The Spring Framework requires J2SE 1.3 and J2EE 1.3 (Servlet 2.3, JSP 1.2, JTA 1.0, EJB 2.0). Integration is provided with Log4J 1.2, CGLIB 1.0, Hibernate 2.0, JDO 1.0, Caucho's Hessian/Burlap 2.1/3.0, JSTL 1.0, Velocity 1.3, and more.

Note: This release uses "com.interface21" as root package, like the original version that came with the book. For the sake of naming consistency, the root package name will change to "org.springframework", starting with 1.0 milestone releases.

Release contents:
* "src" contains the Java source files;
* "dist" contains various Spring Jar files;
* "lib" contains the most important third-party libraries;
* "docs" contains general and API documentation;
* "samples" contains demo application and skeletons.

Latest info is available at the public website: http://www.springframework.org
Project info at the SourceForge site: http://sourceforge.net/projects/springframework

This product includes software developed by the Apache Software Foundation (http://www.apache.org).


3. WHERE TO START?

Preliminary documentation can be in the "docs" directory. Documented application skeletons can be found in "samples/skeletons". Demo applications can be found in "samples/petclinic" and "samples/countries" (with their own readme.txt).

Note: The tutorial in "docs" is a work in progress. It is not fully consistent with the current state of the framework but is still useful as introduction.

The "dist" directory contains the following (overlapping) Jar files for use in applications:
* "spring-beans": just the bean container;
* "spring-jdbc": bean container, AOP framework, transaction framework, JDBC support, O/R Mapping support;
* "spring-full": all Spring framework classes.

"Expert One-on-One J2EE Design and Development" discusses many of Spring's design ideas in detail. Note: The code examples in the book refer to the original framework version that came with the book. Thus, they may need to be adapted to work with the current Spring release.
