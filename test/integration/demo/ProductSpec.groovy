package demo

import grails.plugin.spock.IntegrationSpec

class ProductSpec extends IntegrationSpec {

	def "MultiTenant annotated domain classes are compiled with a tenantId property"() {
		given: new Product(name: "iPhone", tenantId: 2).save(failOnError: true)
		when: def product = Product.findByName("iPhone")
		then: product?.properties['tenantId'] == 2
	}
	
}