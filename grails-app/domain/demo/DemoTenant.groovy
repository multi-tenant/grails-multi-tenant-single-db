package demo

import grails.plugin.multitenant.core.Tenant

/**
 * 
 * @author Kim A. Betti
 */
class DemoTenant implements Tenant {

    static transients = [ "tenantId" ]
    
    String name
    String domain
    Integer mappedTenantId

    static constraints = {
        name blank: false, unique: true
        domain blank: false, unique: true
        mappedTenantId unique: true, min: 0
    }
    
    Integer getTenantId() {
        return this.id;
    }
    
}