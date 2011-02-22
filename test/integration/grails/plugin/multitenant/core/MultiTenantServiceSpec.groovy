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
    
}
