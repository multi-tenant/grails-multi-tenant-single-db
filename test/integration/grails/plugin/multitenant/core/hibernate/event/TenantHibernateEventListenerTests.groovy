package grails.plugin.multitenant.core.hibernate.event

import demo.DemoProduct
import org.junit.Test;

import grails.plugin.multitenant.core.exception.TenantSecurityException;
import grails.test.GrailsUnitTestCase

class TenantHibernateEventListenerTests extends GrailsUnitTestCase {
 
    def tenantUtils
    
    protected void setUp() {
        super.setUp()
        assert tenantUtils
    }

    @Test
    void tenantIdShouldBeInjected() {
        tenantUtils.withTenantId(2) {
            new DemoProduct(name: "iPhone").save(flush: true, failOnError: true)
            DemoProduct.findByName("iPhone").properties['tenantId'] == 2
        }
    }
    
    void shouldNotBeAllowedToLoadOtherTenantsEntities() {
        int iPhoneId = -1
        tenantUtils.withTenantId(2) {
            def iPhone = new DemoProduct(name: "iPhone").save(flush: true, failOnError: true)
            iPhoneId = iPhone.id
        }
        
        tenantUtils.withTenantId(1) {
            DemoProduct.withNewSession {
                def product = DemoProduct.get(iPhoneId);
                assertNull product
            }
        }
    }
    
    @Test
    void shouldBeAllowedToLoadOwnEntities() {
        int iPhoneId = -1
        tenantUtils.withTenantId(2) {
            def iPhone = new DemoProduct(name: "iPhone").save(flush: true, failOnError: true)
            iPhoneId = iPhone.id
        }
        
        tenantUtils.withTenantId(2) {
            DemoProduct.withNewSession {
                def product = DemoProduct.get(iPhoneId);
            }
        }
    }
    
    @Test(expected=TenantSecurityException.class)
    void shouldNotBeAbleToChangeTenantId() {
        def iPhone = null
        tenantUtils.withTenantId(2) {
            iPhone = new DemoProduct(name: "iPhone").save(flush: true, failOnError: true)
        }
        
        tenantUtils.withTenantId(3) {
            iPhone.name = "iPhone 2"
            iPhone.save(flush: true, failOnError: true)
            fail "Should not be able to do update entity with another tenant id"
        }
    }
    
    @Test
    void shouldBeAbleToUpdateWithCurrentId() {
        def iPhone = null
        tenantUtils.withTenantId(2) {
            iPhone = new DemoProduct(name: "iPhone").save(flush: true, failOnError: true)
        }
        
        tenantUtils.withTenantId(2) {
            iPhone.name = "iPhone 2"
            iPhone.save(flush: true, failOnError: true)
        }
    }
    
}
