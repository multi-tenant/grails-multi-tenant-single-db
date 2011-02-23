package grails.plugin.multitenant.core

import grails.plugin.spock.IntegrationSpec
import demo.DemoAnimal;
import demo.DemoProduct
import demo.DemoTenant

/**
 * 
 * @author Kim A. Betti
 */
class MultiTenantServiceSpec extends IntegrationSpec {
    
    def testTenant
    def mulitTenantService
    
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
    
    def "checked exceptions should roll back transaction"() {
        given:
        def product = null
        testTenant.withThisTenant {
            product = new DemoProduct(name: "Some product")
            product.save flush: true, failOnError: true
        }
        
        when:
        testTenant.withThisTenant {
            product.name = "Another name"
            product.save failOnError: true, flush: true
            throw new Exception("Should cause exception rollback")
        }
        
        then:
        Exception ex = thrown()
        
        and:
        product.refresh()
        product.name == "Some product"
    }
    
    def "unchecked exception should also roll back exception"() {
        given:
        def product = null
        testTenant.withThisTenant {
            product = new DemoProduct(name: "Another product")
            product.save flush: true, failOnError: true
        }
        
        when:
        testTenant.withThisTenant {
            product.name = "Another name"
            product.save failOnError: true, flush: true
            throw new RuntimeException("Should cause exception rollback")
        }
        
        then:
        RuntimeException ex = thrown()
        
        and:
        product.refresh()
        product.name == "Another product"
    }
    
    def "do without tenant restrictions"() {
        given: "we create an animal"
        Tenant.withTenantId(123) {
            new DemoAnimal(name: "Pluto").save(failOnError: true)
        }
        
        expect: "other tenants cant see it"
        !Tenant.withTenantId(321) {
            DemoAnimal.findByName("Pluto")
        }
    
        and: "but it should be visible without tenant restrictions"
        Tenant.withoutTenantRestriction {
            DemoAnimal.findByName("Pluto")
        }
    }
    
}
