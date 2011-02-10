MultiTenant - Single Database
=============================

**Important:** This is a very experimental plugin written more or less from scratch together with the supporting [Hawk Eventing](http://github.com/multi-tenant/grails-hawk-eventing) and [Hibernate Hijacker](http://github.com/multi-tenant/grails-hibernate-hijacker) plugins. You might be looking for the re-factored version of the original plugin: [Multi Tenant Core](http://github.com/multi-tenant/grails-multi-tenant-core).

Design goal
-----------

Make it easy to implement multi-tenant applications where all tenants share a single database. 

Per-tenant beans
----------------

I recently added experimental support for per-tenant beans. It's implemented using a custom tenant bean scope in combination with a scoped proxy. 

This feature can be configured through config.groovy. Beans can be flagged as per-tenant by using static fields in the original multi-tenant plugin, this is currently not implemented for this demo / lite version. 

    multiTenant {
        perTenantBeans = [ 'someSpringBean' ]
    }

Tenant configuration events
---------------------------

You might use a domain class to store tenant configuration. Annotating this class with @TenantDomainClass will cause the plugin to trigger events when tenants are added, updated or deleted. This is very useful in situations where you maintain a cache tenant cache for fast lookups or need to do some setup or tear down operations when tenants are added or deleted from your system.

    @TenantDomainClass
    class DomainTenantMap implements Serializable {
        String name, domain // ...
        Integer mappedTenantId

        // ...
    }

### Published events

The following events are published when a tenant has been added, updated or deleted: `tenant.created`, `tenant.updated` and `tenant.deleted`.

### Simple example

Have a look at the [Hawk Eventing](http://www.github.com/multi-tenant/grails-hawk-eventing) project on GitHub for other ways to subscribe to events. 

    eventBroker.subscribe 'tenant.created', { Event evt ->
        println "New tenant: ${evt.payload.name}"
    }

Roadmap / todo:
---------------

### More testing

This refactoring project was initiated because a lot of the intrusive methods used to implement the feature set caused frequent breakages with newer versions of Grails and plugins. I would love to have more automated testing, but a lot of these things are really hard to test. I've been considering whether it would be worth creating a sample project with a set of frequently used plugins + a bunch of integration and functional tests. 
