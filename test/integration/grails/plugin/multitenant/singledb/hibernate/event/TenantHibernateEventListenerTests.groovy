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
            def iPhone = new DemoProduct(name: "iPhone").save(flush: true, failOnError: true)
            iPhoneId = iPhone.id
        }
        
        DemoTenant.withTenantId(1) {
            DemoProduct.withNewSession {
                def product = DemoProduct.get(iPhoneId);
                assertNull product
            }
        }
    }
    
    @Test
    void shouldBeAllowedToLoadOwnEntities() {
        int iPhoneId = -1
        DemoTenant.withTenantId(2) {
            def iPhone = new DemoProduct(name: "iPhone").save(flush: true, failOnError: true)
            iPhoneId = iPhone.id
        }
        
        DemoTenant.withTenantId(2) {
            DemoProduct.withNewSession {
                def product = DemoProduct.get(iPhoneId);
            }
        }
    }
    
    @Test(expected=TenantSecurityException.class)
    void shouldNotBeAbleToChangeTenantId() {
        def iPhone = null
        DemoTenant.withTenantId(2) {
            iPhone = new DemoProduct(name: "iPhone").save(flush: true, failOnError: true)
        }
        
        DemoTenant.withTenantId(3) {
            iPhone.name = "iPhone 2"
            iPhone.save(flush: true, failOnError: true)
            fail "Should not be able to do update entity with another tenant id"
        }
    }
    
    @Test
    void shouldBeAbleToUpdateWithCurrentId() {
        def iPhone = null
        DemoTenant.withTenantId(2) {
            iPhone = new DemoProduct(name: "iPhone").save(flush: true, failOnError: true)
        }
        
        DemoTenant.withTenantId(2) {
            iPhone.name = "iPhone 2"
            iPhone.save(flush: true, failOnError: true)
        }
    }
    
}
