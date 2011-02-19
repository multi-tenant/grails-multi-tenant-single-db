package grails.plugin.multitenant.singledb.hibernate.event

import grails.plugin.multitenant.core.exception.TenantSecurityException
import grails.test.GrailsUnitTestCase

import org.junit.Test

import demo.DemoProduct
import demo.DemoTenant

/**
 * 
 * @author Kim A. Betti
 */
class TenantHibernateEventListenerTests extends GrailsUnitTestCase {
    
    static transactional = false
 
    @Test
    void tenantIdShouldBeInjected() {
        DemoTenant.withTenantId(2) {
            new DemoProduct(name: "iPhone").save(flush: true, failOnError: true)
            DemoProduct.findByName("iPhone").properties['tenantId'] == 2
        }
    }
    
    void shouldNotBeAllowedToLoadOtherTenantsEntities() {
        int iPhoneId = -1
        DemoTenant.withTenantId(2) {
            def iBone = new DemoProduct(name: "iBone").save(flush: true, failOnError: true)
            iBoneId = iBone.id
        }
        
        DemoTenant.withTenantId(1) {
            DemoProduct.withNewSession {
                def product = DemoProduct.get(iBoneId);
                assertNull product
            }
        }
    }
    
    @Test
    void shouldBeAllowedToLoadOwnEntities() {
        int entityId = -1
        DemoTenant.withTenantId(2) {
            def productInstance = new DemoProduct(name: "product-name-goes-here").save(flush: true, failOnError: true)
            entityId = productInstance.id
        }
        
        DemoTenant.withTenantId(2) {
            DemoProduct.withNewSession {
                def product = DemoProduct.get(entityId);
            }
        }
    }
    
    @Test(expected=TenantSecurityException.class)
    void shouldNotBeAbleToChangeTenantId() {
        def productInstance = null
        DemoTenant.withTenantId(2) {
            productInstance = new DemoProduct(name: "another-product-name").save(flush: true, failOnError: true)
        }
        
        DemoTenant.withTenantId(3) {
            productInstance.name = "another-product-name-version-2"
            productInstance.save(flush: true, failOnError: true)
            fail "Should not be able to do update entity with another tenant id"
        }
    }
    
    @Test
    void shouldBeAbleToUpdateWithCurrentId() {
        def productInstance = null
        DemoTenant.withTenantId(2) {
            productInstance = new DemoProduct(name: "yet-another-product-name").save(flush: true, failOnError: true)
        }
        
        DemoTenant.withTenantId(2) {
            productInstance.name = "yet-another-product-name-version-2"
            productInstance.save(flush: true, failOnError: true)
        }
    }
    
}
