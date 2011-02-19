package demo

import grails.test.*

import org.junit.Test
import org.springframework.dao.DataIntegrityViolationException

/**
 * 
 * @author Kim A. Betti
 */
class DemoProductTests extends GrailsUnitTestCase {

    static transactional = false
    
    @Test
    void differentTenantsCanUseSameValueForUniqueProperty() {
        DemoTenant.withTenantId(1) {
            new DemoProduct(name: 'bPhone').save(flush: true, failOnError: true)
            assertEquals 1, DemoProduct.findAllByName("bPhone").size()
        }

        DemoTenant.withTenantId(2) {
            new DemoProduct(name: 'bPhone').save(flush: true, failOnError: true)
        }
    }

    @Test(expected=DataIntegrityViolationException.class)
    void singleTenantCanOnlyUseOneValueForUniqueProperty() {
        DemoTenant.withTenantId(1) {
            new DemoProduct(name: 'bPad').save(flush: true, failOnError: true)
        }

        DemoTenant.withTenantId(1) {
            new DemoProduct(name: 'bPad').save(flush: true, failOnError: true)
            fail "This should not be allowed"
        }
    }


    @Test
    void filtersWorksForHql() {
        String hql = "from DemoProduct as p where p.name = 'Flying car'"

        DemoTenant.withTenantId(1) {
            new DemoProduct(name: 'Flying car').save(flush: true, failOnError: true)
            assertEquals 1, DemoProduct.findAll(hql).size()
        }

        DemoTenant.withTenantId(2) {
            assertEquals 0, DemoProduct.findAll(hql).size()
        }
    }
}
