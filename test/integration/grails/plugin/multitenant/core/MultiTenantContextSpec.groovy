package grails.plugin.multitenant.core

import grails.plugin.spock.IntegrationSpec
import demo.DemoTenant

/**
 * 
 * @author Kim A. Betti
 */
class MultiTenantContextSpec extends IntegrationSpec {
    
    def multiTenantContext

    def "finds domain class implementing the Tenant interface"() {
        when:
        Class<? extends Tenant> tenantClass = multiTenantContext.findTenantClass()
        
        then:
        tenantClass == DemoTenant
    }
    
}
