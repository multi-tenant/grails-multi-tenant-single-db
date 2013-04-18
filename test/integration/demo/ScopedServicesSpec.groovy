package demo

import grails.plugin.multitenant.core.Tenant
import grails.plugin.spock.IntegrationSpec

/**
 * Mostly as a smoke test to detect if any multi-tenant
 * code affects regular domain classes.
 * @author Kim A. Betti
 */
class ScopedServicesSpec extends IntegrationSpec {

    def demoService
    def anotherDemoService

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

    def "tenant scope is working in service"() {
        given: "each tenant set a val on service"
            Tenant.withTenantId 1, {
                assert "none" == anotherDemoService.touchedByTenant
                anotherDemoService.touchedByTenant = "Tenant-1"
            }

            Tenant.withTenantId 2, {
                assert "none" == anotherDemoService.touchedByTenant
                anotherDemoService.touchedByTenant = "Tenant-2"
            }

        expect: "we should get values that were set"
            Tenant.withTenantId 1, {
                println anotherDemoService.touchedByTenant
                "Tenant-1" == anotherDemoService.touchedByTenant
            }
            Tenant.withTenantId 2, {
                println anotherDemoService.touchedByTenant
                "Tenant-2" == anotherDemoService.touchedByTenant
            }
    }
}
