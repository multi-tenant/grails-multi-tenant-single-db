package demo

import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import grails.validation.ValidationException
import grails.test.*

class DemoProductTests extends GrailsUnitTestCase {
	
	def tenantUtils
    
	@Test
    void differentTenantsCanUseSameValueForUniqueProperty() {
		tenantUtils.withTenantId(1) {
			new DemoProduct(name: 'bPhone').save(flush: true, failOnError: true)
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
	
}
