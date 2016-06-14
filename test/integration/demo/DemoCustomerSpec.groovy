package demo

import grails.test.spock.IntegrationSpec

/**
 * Mostly as a smoke test to detect if any multi-tenant
 * code affects regular domain classes.
 * @author Kim A. Betti
 */
class DemoCustomerSpec extends IntegrationSpec {
    //FIXME This used to work? I think this should work without needing to turn transactional off but it does not.
    //if its transactional then the session in the withThisTenant cannot see the customer
    static transactional = false

    def testTenant

    def setup() {
        testTenant = new DemoTenant(name: "DemoCustomerSpec tenant", domain: "DemoCustomerSpec.com")
        testTenant.save(flush: true, failOnError: true)
    }

    def "instances are available for tenants"() {
        given: "a customer instance created outside a tenant namespace"
        def c = new DemoCustomer(name: "DemoCustomerSpec A").save(failOnError: true, flush: true)
        assert c
        println c.id

        expect: "we should be able to look up the customer inside a tenant namespace"
        assert DemoCustomer.findByName("DemoCustomerSpec A")
        testTenant.withThisTenant {
            DemoCustomer.get(1)
        }
        testTenant.withThisTenant {
            DemoCustomer.findByName("DemoCustomerSpec A")
        }
    }
}
