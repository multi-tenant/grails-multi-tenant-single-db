package demo

import grails.plugin.multitenant.core.Tenant

/**
 * Mostly as a smoke test to detect if any multi-tenant
 * code affects regular domain classes.
 * @author Kim A. Betti
 */
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(DemoService)
//@TestFor(AnotherDemoService)
class DemoServiceSpec extends Specification {

    def demoService
   

    def "tenant scope is working from config"() {
        given: "each tenant set a val on service"
            Tenant.withTenantId 1, {
                assert "none" == demoService.touchedByTenant
                demoService.touchedByTenant = "Tenant-1"
            }

            Tenant.withTenantId 2, {
                assert "none" == demoService.touchedByTenant
                demoService.touchedByTenant = "Tenant-2"
            }

        expect: "we should get values that were set"
            Tenant.withTenantId 1, {
                println demoService.touchedByTenant
                "Tenant-1" == demoService.touchedByTenant
            }
            Tenant.withTenantId 2, {
                println demoService.touchedByTenant
                "Tenant-2" == demoService.touchedByTenant
            }
    }

    
}
