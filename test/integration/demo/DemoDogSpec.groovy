package demo

import grails.plugin.multitenant.core.MultiTenantDomainClass
import grails.plugin.multitenant.core.Tenant


/**
 * Dog extends Animal, but it's not stamped with the @MultiTenant
 * annotation as Aminal is. These test cases makes sure that this works.
 *
 * @author Kim A. Betti
 */
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(DemoDog)
class DemoDogSpec extends Specification {

	def tenant
	def tenant2
    
    def setup() {
        tenant = new DemoTenant(name:'Demo Tenant',domain:'demotenant.org',id:6)
        tenant.save(failOnError:true,flush:true)
		tenant2 = new DemoTenant(name:'Demo Tenant2',domain:'demotenant2.com',id:7)
        tenant2.save(failOnError:true,flush:true)
        
    }

    
	

    def "dog should implement the MultiTenantDomainClass interface"() {
        expect:
        MultiTenantDomainClass.isAssignableFrom DemoDog
    }

    def "should work with inheritance"() {
        when: "we create a dog instance in the namespace of tenant 1"
        Tenant.withTenantId(6){
            def pluto = new DemoDog(name: "Pluto")
            pluto.save flush: true, failOnError: true
			print "pluto = ${pluto.tenantId}"
        }

        then: "we can look it up again in the same namespace"
        Tenant.withTenantId(6){
            DemoDog.findByName("Pluto")
        } != null

        and: "it should not be visible from another tenants namespace"
        Tenant.withTenantId(7) {
            DemoDog.findByName("Pluto")
        } == null
    }
	
	def cleanup() {
		tenant = null
		tenant2 = null
    }
}
