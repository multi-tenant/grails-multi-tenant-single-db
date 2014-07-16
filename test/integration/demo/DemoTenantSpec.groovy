package demo

import grails.plugin.spock.IntegrationSpec
import grails.plugins.hawkeventing.Event
import grails.plugins.hawkeventing.EventBroker
import grails.plugins.hawkeventing.EventConsumer
import org.springframework.transaction.support.DefaultTransactionDefinition

/**
 * @author Kim A. Betti
 */
class DemoTenantSpec extends IntegrationSpec {

    EventBroker eventBroker

    // Workaround for GRAILS-9771
    def transactionManager_secondary
    def transactionStatus

    def setup() {
        transactionStatus = transactionManager_secondary.getTransaction(new DefaultTransactionDefinition())
    }

    def cleanup() {
        transactionManager_secondary.rollback(transactionStatus)
    }

    def "New tenants triggers events" () {
        given: "Subscription to the expected event"
        def newTenantConsumer = Mock(EventConsumer)
        eventBroker.subscribe("tenant.created", newTenantConsumer)

        when: "We make a new tenant"
        new DemoTenant(name: "Mr. Bond", domain: "james.mi6.com", mappedTenantId: 7)
                .save(failOnError: true, flush: true)

        then: "One event has occured"
        1 * newTenantConsumer.consume(_ as Event)
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
        1 * updatedTenantConsumer.consume(_ as Event)
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
        1 * deletedTenantConsumer.consume(_ as Event)
    }

}