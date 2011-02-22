package demo

import grails.plugin.spock.IntegrationSpec;

/**
 * Mostly as a smoke test to detect if any multi-tenant
 * code affects regular domain classes.
 * @author Kim A. Betti
 */
class DemoCustomerSpec extends IntegrationSpec {

    def testTenant
    
    def setup() {
        testTenant = new DemoTenant(name: "test tenant", domain: "test.com")
        testTenant.save flush: true, failOnError: true
    }
    
    def "instances are available for tenants"() {
        expect: "we should be able to look up the customer inside a tenant namespace"
        new DemoCustomer(name: "Customer A").save(failOnError: true, flush: true)
        testTenant.withThisTenant {
            DemoCustomer.findByName("Customer A")
        }
    }
    
}