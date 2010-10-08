Grails Multi-Tenant Core (lite)
===============================

This is a very experimental plugin depending on [Grails Eventing](http://github.com/multi-tenant/grails-eventing) and [Hibernate Hijacker](http://github.com/multi-tenant/grails-hibernate-hijacker). 

Roadmap / todo:
---------------

 * Implement consistent error handling / exception hierarchy, especially in Hibernate event listeners for @MultiTenant annotated domain classes.
 * Write more tests (some of these things are quite hard to write good tests for).
