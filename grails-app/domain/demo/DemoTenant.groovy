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

    static constraints = {
        name blank: false, unique: true
        domain blank: false, unique: true
    }
    
    Integer tenantId() {
        return this.id;
    }
    
}