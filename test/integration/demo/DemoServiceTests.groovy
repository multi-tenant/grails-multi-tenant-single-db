package demo

import org.junit.Test;

import grails.plugin.multitenant.core.util.TenantUtils
import grails.test.*

class DemoServiceTests extends GrailsUnitTestCase {
	
	def tenantUtils
	def demoService
	
	@Test
    void shouldCreateProxyForPerTenantBeans() {
		//println " -?? " + demoService.touchedByTenant
		
		tenantUtils.withTenantId(1) {
			assertEquals "none", demoService.touchedByTenant
			demoService.touchedByTenant = "Tenant-1"
		}
		
		tenantUtils.withTenantId(2) {
			assertEquals "none", demoService.touchedByTenant
			demoService.touchedByTenant = "Tenant-2"
		}
		
		tenantUtils.withTenantId(1) {
			assertEquals "Tenant-1", demoService.touchedByTenant
		}
		
    }
	
}