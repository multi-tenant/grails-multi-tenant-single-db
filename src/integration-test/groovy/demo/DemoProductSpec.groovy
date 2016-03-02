package demo

import grails.plugin.multitenant.core.Tenant

import org.springframework.dao.DataIntegrityViolationException

/**
 * @author Kim A. Betti
 */

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
import grails.test.mixin.integration.Integration
import grails.transaction.*
import spock.lang.*

@Integration
@Rollback
class DemoProductSpec extends Specification {

    static transactional = false

    def "different tenants can use the same value for unique properties"() {
        when:
        Tenant.withTenantId(1) {
            new DemoProduct(name: 'bPhone').save(flush: true, failOnError: true)
        }

        then:
        Tenant.withTenantId(1) {
            DemoProduct.findAllByName("bPhone").size()
        } == 1

        and:
        Tenant.withTenantId(2) {
            new DemoProduct(name: 'bPhone').save(flush: true, failOnError: true)
        }
    }

    def "tenants cant break a unqiue constraint"() {
        given:
        Tenant.withTenantId(1) {
            new DemoProduct(name: 'bPad').save(flush: true, failOnError: true)
        }

        when:
        Tenant.withTenantId(1) {
            new DemoProduct(name: 'bPad').save(flush: true, failOnError: true)
            assert false, "This should not be allowed"
        }

        then:
        DataIntegrityViolationException ex = thrown()
    }


    def "filters works for hql"() {
        given:
        String hql = "from DemoProduct as p where p.name = 'Flying car'"

        when:
        Tenant.withTenantId(1) {
            new DemoProduct(name: 'Flying car').save(flush: true, failOnError: true)
        }

        then:
        Tenant.withTenantId(1) {
            DemoProduct.findAll(hql).size()
        } == 1

        and:
        Tenant.withTenantId(2) {
            DemoProduct.findAll(hql).size()
        } == 0
    }
}
