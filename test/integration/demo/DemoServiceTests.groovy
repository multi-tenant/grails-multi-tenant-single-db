package demo

import grails.plugin.multitenant.core.Tenant;
import grails.test.*

import org.junit.Test

/**
 * Verify that a tenant-scoped proxy is created around
 * beans configured as per-tenant. 
 * @author Kim A. Betti <kim@developer-b.com>
 */
class DemoServiceTests extends GrailsUnitTestCase {

    def demoService

    @Test
    void shouldCreateProxyForPerTenantBeans() {

        Tenant.withTenantId 1, {
            assertEquals "none", demoService.touchedByTenant
            demoService.touchedByTenant = "Tenant-1"
        }

        Tenant.withTenantId 2, {
            assertEquals "none", demoService.touchedByTenant
            demoService.touchedByTenant = "Tenant-2"
        }

        Tenant.withTenantId 1, { 
            assertEquals "Tenant-1", demoService.touchedByTenant
        }
    }
}