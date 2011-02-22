package demo

import grails.plugin.multitenant.core.annotation.MultiTenant

@MultiTenant
class DemoProduct {

    String name

    static constraints = {
        name unique: 'tenantId', nullable: false, blank: false
    }
    
}