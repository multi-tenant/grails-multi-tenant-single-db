package demo

import grails.plugin.multitenant.core.annotation.TenantDomainClass

@TenantDomainClass
class DemoTenant {

    String name
    String domain
    Integer mappedTenantId

    static constraints = {
        name blank: false, unique: true
        domain blank: false, unique: true
        mappedTenantId unique: true, min: 0
    }
}