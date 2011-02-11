package grails.plugin.multitenant.core.util

import grails.plugin.spock.UnitSpec
import demo.DemoAnimal
import demo.DemoDog

class TenantUtilsSpec extends UnitSpec {

    def "MultiTenant annotation is detected on parent"() {
        expect:
        TenantUtils.isMultiTenantClass(DemoAnimal)
        TenantUtils.isMultiTenantClass(DemoDog)

        and:
        !TenantUtils.isMultiTenantClass(Integer)
    }
    
}
