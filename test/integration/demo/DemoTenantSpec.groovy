package demo

import spock.lang.*
import grails.plugin.eventing.EventConsumer;
import grails.plugin.multitenant.core.annotation.TenantDomainClass;
import grails.plugin.multitenant.core.util.TenantUtils;
import grails.plugin.spock.*

class DemoTenantSpec extends IntegrationSpec {
	
	def eventBroker

	
	def "New tenants triggers events" () {
		given: "Subscription to the expected event"
		def newTenantConsumer = Mock(EventConsumer)
		eventBroker.subscribe("tenant.created", newTenantConsumer)
		
		when: "We make a new tenant"
		new DemoTenant(name: "Mr. Bond", domain: "james.mi6.com", mappedTenantId: 7)
			.save(failOnError: true, flush: true)
		
		then: "One event has occured"
		1 * newTenantConsumer.consume(_ as DemoTenant, _)
	}
	
	def "Changes to a tenant triggers event" () {
		given: "Subscription to the expected event"
		def updatedTenantConsumer = Mock(EventConsumer)
		eventBroker.subscribe("tenant.updated", updatedTenantConsumer)
		
		when: "We make a new tenant"
		def james = new DemoTenant(name: "Mr. Bond", domain: "james.mi6.com", mappedTenantId: 7)
			.save(failOnError: true, flush: true)
		
		and: "Update it"
		james.name = "007"
		james.save(flush: true, failOnError: true)
			
		then: "One event has occured"
		1 * updatedTenantConsumer.consume(_ as DemoTenant, _)
	}
	
	def "Deleting a tenant triggers event" () {
		given: "Subscription to the expected event"
		def deletedTenantConsumer = Mock(EventConsumer)
		eventBroker.subscribe("tenant.deleted", deletedTenantConsumer)
		
		when: "We make a new tenant"
		def james = new DemoTenant(name: "Mr. Bond", domain: "james.mi6.com", mappedTenantId: 7)
			.save(failOnError: true, flush: true)
		
		and: "Delete it"
		james.delete(flush: true, failOnError: true)
			
		then: "One event has occured"
		1 * deletedTenantConsumer.consume(_ as DemoTenant, _)
	}
	
}