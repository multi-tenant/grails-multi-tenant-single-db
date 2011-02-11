package grails.plugin.multitenant.singledb.util

import demo.DemoDog
import demo.DemoAnimal
import grails.plugin.spock.UnitSpec

class TenantUtilsSpec extends UnitSpec {

	def "MultiTenant annotation is detected on parent"() {
		expect:
			TenantUtils.isMultiTenantClass(DemoAnimal)
			TenantUtils.isMultiTenantClass(DemoDog)
			
		and:
			!TenantUtils.isMultiTenantClass(Integer)
	}
	
}
