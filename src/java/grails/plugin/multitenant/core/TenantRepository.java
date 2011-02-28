package grails.plugin.multitenant.core;

import grails.plugin.multitenant.core.exception.TenantNotFoundException;

/**
 * Most applications will probably provide an implementation of this interface.
 * Having this contract makes it possible to look up tenant instances across
 * plugins and applications in a type safe way.
 * 
 * @author Kim A. Betti
 */
public interface TenantRepository {

    /**
     * Look up a tenant instance associated with a given id.
     * @param tenantId This will often be a database PK
     * @return The tenant instance associated with the tenant id
     * @throws TenantNotFoundException
     */
    Tenant findByTenantId(Integer tenantId) throws TenantNotFoundException;

}