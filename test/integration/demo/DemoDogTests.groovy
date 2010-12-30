package demo

import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import grails.validation.ValidationException
import grails.test.*

class DemoDogTests extends GrailsUnitTestCase {
	
	def tenantUtils
    
	@Test
    void shouldWorkWithInheritance() {
		tenantUtils.withTenantId(1) {
			def pluto = new DemoDog(name: "Pluto")
			pluto.save(flush: true, failOnError: true)
			assertNotNull DemoDog.findByName("Pluto")
		}
		
		tenantUtils.withTenantId(2) {
			assertNull DemoDog.findByName("Pluto")
		}
    }
	
}
