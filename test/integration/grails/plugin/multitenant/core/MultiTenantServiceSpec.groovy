package grails.plugin.multitenant.core

import grails.plugin.spock.IntegrationSpec
import demo.DemoProduct
import demo.DemoTenant

/**
 * 
 * @author Kim A. Betti
 */
class MultiTenantServiceSpec extends IntegrationSpec {
    
    def testTenant
    
    def setup() {
        testTenant = new DemoTenant(name: "test tenant", domain: "test.com")
        testTenant.save flush: true, failOnError: true
    }

    def "withThisTenant should work"() {
        when:
        boolean closureHasExecuted = false
        testTenant.withThisTenant {
            closureHasExecuted = true
        }
        
        then:
        closureHasExecuted
    }
    
}
