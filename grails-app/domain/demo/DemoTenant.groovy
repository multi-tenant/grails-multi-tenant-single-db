package demo

import grails.plugin.multitenant.core.Tenant

/**
 * @author Kim A. Betti
 */
class DemoTenant implements Tenant {

    String name
    String domain

    static constraints = {
        name blank: false, unique: true
        domain blank: false, unique: true
    }

    Long tenantId() {
        id
    }
}
