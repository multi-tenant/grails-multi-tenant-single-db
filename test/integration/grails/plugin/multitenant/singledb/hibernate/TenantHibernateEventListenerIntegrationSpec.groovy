package grails.plugin.multitenant.singledb.hibernate

import grails.plugin.multitenant.core.Tenant
import grails.plugin.multitenant.core.exception.TenantSecurityException
import grails.plugin.spock.IntegrationSpec
import demo.DemoProduct

/**
 *
 * @author Kim A. Betti
 */
class TenantHibernateEventListenerIntegrationSpec extends IntegrationSpec {
    
    static transactional = false
    
    def "tenant id should be injected"() {
        when:
        Tenant.withTenantId(2) {
            new DemoProduct(name: "iPhone").save(flush: true, failOnError: true)
        }
        
        then:
        Tenant.withTenantId(2) {
            DemoProduct.findByName("iPhone").properties['tenantId']
        } == 2
    }
    
    def "should not be allowed to load entities owned by other tenants"() {
        when: "we create a new product iBone"
        int iBoneId = Tenant.withTenantId(2) {
            DemoProduct iBone = new DemoProduct(name: "iBone").save(flush: true, failOnError: true);
            return iBone.id
        }
        
        then: "the id should not be negative"
        iBoneId > -1
        
        and: "iBone should not be viewable by other tenants"
        Tenant.withTenantId(1) {
            DemoProduct.withNewSession {
                DemoProduct.get(iBoneId);
            }
        } == null
    }
    
    def "should be allowed to load own entities"() {
        when:
        int entityId = Tenant.withTenantId(2) {
            def productInstance = new DemoProduct(name: "product-name-goes-here").save(flush: true, failOnError: true)
            return productInstance.id
        }
        
        then:
        Tenant.withTenantId(2) {
            DemoProduct.withNewSession {
                DemoProduct.get(entityId);
            }
        } != null
    }
    
    def "should not be able to change tenant id"() {
        given:
        def productInstance = Tenant.withTenantId(2) {
            new DemoProduct(name: "another-product-name").save(flush: true, failOnError: true)
        }
        
        when:
        Tenant.withTenantId(3) {
            productInstance.name = "another-product-name-version-2"
            productInstance.save(flush: true, failOnError: true)
            throw new Exception("Should not be able to do update entity with another tenant id")
        }
        
        then:
        TenantSecurityException ex = thrown()
    }
    
    def "should be able to update with current id"() {
        when:
        def productInstance = Tenant.withTenantId(2) {
            new DemoProduct(name: "yet-another-product-name").save(flush: true, failOnError: true)
        }
        
        then:
        Tenant.withTenantId(2) {
            productInstance.name = "yet-another-product-name-version-2";
            productInstance.save(flush: true, failOnError: true)
        }
    }
    
}
