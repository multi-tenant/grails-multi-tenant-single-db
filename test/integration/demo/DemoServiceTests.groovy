package demo

import org.junit.Test;

import grails.plugin.multitenant.singledb.util.TenantUtils
import grails.test.*

/**
 * Verify that a tenant-scoped proxy is created around
 * beans configured as per-tenant. 
 * @author Kim A. Betti <kim@developer-b.com>
 */
class DemoServiceTests extends GrailsUnitTestCase {
	
	def tenantUtils
	def demoService
	
	@Test
    void shouldCreateProxyForPerTenantBeans() {

		tenantUtils.withTenantId 1, {
			assertEquals "none", demoService.touchedByTenant
			demoService.touchedByTenant = "Tenant-1"
		}
		
		tenantUtils.withTenantId 2, {
			assertEquals "none", demoService.touchedByTenant
			demoService.touchedByTenant = "Tenant-2"
		}
		
		tenantUtils.withTenantId 1, {
			assertEquals "Tenant-1", demoService.touchedByTenant
		}
		
    }
	
}