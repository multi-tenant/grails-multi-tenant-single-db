package grails.plugin.multitenant.core

import grails.plugin.spock.UnitSpec
import demo.DemoAnimal
import demo.DemoDog

/**
 * 
 * @author Kim A. Betti
 */
class MultiTenantContextSpec extends UnitSpec {

    MultiTenantContext mtCtx = new MultiTenantContext()
    
    def "MultiTenant annotation is detected on parent"() {
        expect:
        mtCtx.hasMultiTenantAnnotation(DemoAnimal)
        mtCtx.hasMultiTenantAnnotation(DemoDog)

        and:
        !mtCtx.hasMultiTenantAnnotation(Integer)
    }
    
}