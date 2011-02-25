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

    Tenant findByTenantId(Integer tenantId) throws TenantNotFoundException;

    void setTenantClass(Class<? extends Tenant> tenantClass);

    Class<? extends Tenant> getTenantClass();

}