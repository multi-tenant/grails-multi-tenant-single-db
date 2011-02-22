package demo

import grails.plugin.multitenant.core.Tenant;
import grails.test.GrailsUnitTestCase

import org.junit.Test

/**
 * 
 * @author Kim A. Betti
 */
class DemoDogTests extends GrailsUnitTestCase {

    @Test
    void shouldWorkWithInheritance() {
        int count = 0
        
        Tenant.withTenantId(1) {
            def pluto = new DemoDog(name: "Pluto")
            pluto.save(flush: true, failOnError: true)
            assertNotNull DemoDog.findByName("Pluto")
            
            ++count
        }

        Tenant.withTenantId(2) {
            assertNull DemoDog.findByName("Pluto")
            ++count
        }
        
        assertEquals "Closures not invoked", 2, count
    }
    
}
