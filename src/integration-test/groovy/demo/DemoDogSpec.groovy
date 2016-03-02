package demo

import grails.plugin.multitenant.core.MultiTenantDomainClass
import grails.plugin.multitenant.core.Tenant


/**
 * Dog extends Animal, but it's not stamped with the @MultiTenant
 * annotation as Aminal is. These test cases makes sure that this works.
 *
 * @author Kim A. Betti
 */
import grails.test.mixin.integration.Integration
import grails.transaction.*
import spock.lang.*

@Integration
//@Rollback
class DemoDogSpec extends Specification {

    def setup() {
        
    }
    def "dog should implement the MultiTenantDomainClass interface"() {
        expect:
        MultiTenantDomainClass.isAssignableFrom DemoDog
    }

    def "should work with inheritance"() {
        when: "we store create a dog instance in the namespace of tenant 1"
        Tenant.withTenantId(1) {
            def pluto = new DemoDog(name: "Pluto")
            pluto.save flush: true, failOnError: true
        }

        then: "we can look it up again in the same namespace"
        Tenant.withTenantId(1) {
            DemoDog.findByName("Pluto")
        } != null

        and: "it should not be visible from another tenants namespace"
        Tenant.withTenantId(2) {
            DemoDog.findByName("Pluto")
        } == null
    }
}
