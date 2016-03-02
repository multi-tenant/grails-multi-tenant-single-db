package grails.plugin.multitenant.core.resolve;

import grails.plugin.multitenant.core.exception.TenantResolveException;

import javax.servlet.http.HttpServletRequest;

/**
 * Resolves tenant id based on http request.
 * Each application should provide their implementation of this.
 * The implementation then have to be registered as a Spring bean
 * named 'tenantResolver'. Have a look at the documentation on the
 * bean dsl for more information on how to do this.
 * 
 * TODO: Make a more pluggable / configurable approach to how the current tenant is looked up
 * 
 * @see http://grails.org/doc/latest/guide/single.html#14.%20Grails%20and%20Spring
 */
public interface TenantResolver {

    /**
     * 
     * @param request
     * @return tenant id, don't return 'null' unless you know what you're doing
     * @throws TenantResolveException
     */
    Integer resolve(HttpServletRequest request) throws TenantResolveException;

}