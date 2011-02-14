package grails.plugin.multitenant.core;

/**
 * Most applications will have one domain class implementing
 * this interface and the getTenantId method will most likely
 * return the database generated id to allow for fast lookups
 * from second level cache.
 * 
 * @author Kim A. Betti
 */
public interface Tenant {

    Integer getTenantId();

}