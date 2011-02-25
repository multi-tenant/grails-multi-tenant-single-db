package grails.plugin.multitenant.core.impl

import demo.DemoTenant
import grails.plugin.spock.IntegrationSpec

/**
 * 
 * @author Kim A. Betti
 */
class DefaultTenantRepositorySpec extends IntegrationSpec {

    def tenantRepository
    
    def "DefaultTenantRepository should be used as a fallback when no other implementation is provided"() {
        expect:
        tenantRepository != null
        tenantRepository instanceof DefaultTenantRepository
    }
    
    def "it should detect DemoTenant by scanning domain classes"() {
        expect:
        tenantRepository.tenantClass == DemoTenant
    }
    
}
