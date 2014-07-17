package demo

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(DemoCustomer)
class DemoCustomerSpec extends Specification {
    def tenant
    
    def setup() {
        tenant = new DemoTenant(name:'Demo Tenant',domain:'demotenant.org')
        tenant.save(failOnError:true,flush:true)
        def customer = new DemoCustomer(name:'Demo Customer')
        customer.save(failOnError:true,flush:true)
    }

    

    def "instances are available for tenants"() {
        given: "a customer instance created outside a tenant namespace"
            
                
        expect: "we should be able to look up the customer inside a tenant namespace"
            
            assert DemoCustomer.findByName("Demo Customer")
            tenant.withThisTenant{
                DemoCustomer.get(1)
            }
        
            tenant.withThisTenant{
                DemoCustomer.findByName("Demo Customer")
            }
    }
	
	def cleanup() {
		tenant = null
		customer = null
    }
}
