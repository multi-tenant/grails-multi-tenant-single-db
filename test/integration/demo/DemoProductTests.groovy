package demo

import grails.test.*

import org.junit.Test
import org.springframework.dao.DataIntegrityViolationException

class DemoProductTests extends GrailsUnitTestCase {

    def tenantUtils

    @Test
    void differentTenantsCanUseSameValueForUniqueProperty() {
        tenantUtils.withTenantId(1) {
            new DemoProduct(name: 'bPhone').save(flush: true, failOnError: true)
            assertEquals 1, DemoProduct.findAllByName("bPhone").size()
        }

        tenantUtils.withTenantId(2) {
            new DemoProduct(name: 'bPhone').save(flush: true, failOnError: true)
        }
    }

    @Test(expected=DataIntegrityViolationException.class)
    void singleTenantCanOnlyUseOneValueForUniqueProperty() {
        tenantUtils.withTenantId(1) {
            new DemoProduct(name: 'bPhone').save(flush: true, failOnError: true)
        }

        tenantUtils.withTenantId(1) {
            new DemoProduct(name: 'bPhone').save(flush: true, failOnError: true)
            fail "This should not be allowed"
        }
    }


    @Test
    void filtersWorksForHql() {
        String hql = "from DemoProduct as p where p.name = 'Flying car'"

        tenantUtils.withTenantId(1) {
            new DemoProduct(name: 'Flying car').save(flush: true, failOnError: true)
            assertEquals 1, DemoProduct.findAll(hql).size()
        }

        tenantUtils.withTenantId(2) {
            assertEquals 0, DemoProduct.findAll(hql).size()
        }
    }
}
