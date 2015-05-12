package grails.plugin.multitenant.core;

/**
 * Most applications will have one domain class implementing
 * this interface and the tenantId method will most likely
 * return the database generated id to allow for fast lookups
 * from second level cache.
 * 
 * @author Kim A. Betti
 */
public interface Tenant {

    /**
     * This interface will often be implemented Grails domain classes.
     * Naming this method by Java bean convention (getTenantId) will cause
     * GORM to mistake it for a property and fail.
     * @return tenant id, will often be a database PK
     */
    Integer tenantId();

}