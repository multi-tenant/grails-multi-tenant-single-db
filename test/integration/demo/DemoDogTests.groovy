package demo

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
        
        DemoTenant.withTenantId(1) {
            def pluto = new DemoDog(name: "Pluto")
            pluto.save(flush: true, failOnError: true)
            assertNotNull DemoDog.findByName("Pluto")
            
            ++count
        }

        DemoTenant.withTenantId(2) {
            assertNull DemoDog.findByName("Pluto")
            ++count
        }
        
        assertEquals "Closures not invoked", 2, count
    }
    
}
