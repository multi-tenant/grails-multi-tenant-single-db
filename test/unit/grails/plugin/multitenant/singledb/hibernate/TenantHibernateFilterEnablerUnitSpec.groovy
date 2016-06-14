package grails.plugin.multitenant.singledb.hibernate

import grails.plugin.multitenant.core.CurrentTenant
import grails.plugins.hawkeventing.BaseEvent
import grails.plugins.hawkeventing.Event
import org.hibernate.Filter
import org.hibernate.Session
import org.hibernate.engine.spi.FilterDefinition
import spock.lang.Specification

/**
 * @author Kim A. Betti
 */
class TenantHibernateFilterEnablerUnitSpec extends Specification {

    TenantHibernateFilterEnabler filterEnabler
    FilterDefinition filterDefinition = new FilterDefinition("multiTenantHibernateFilter", ":tenantId = tennatId", ["tenantId": "java.lang.Integer"])

    def setup() {
        filterEnabler = new TenantHibernateFilterEnabler()
        filterEnabler.multiTenantHibernateFilter = filterDefinition
    }

    def "filter is activated and configured when a new session is created and current tenant is set"() {
        given: "a mocked Hibernate session and a new session event"
        Session mockedSession = Mock()
        Event newSessionEvent = new BaseEvent("hibernate.sessionCreated", mockedSession)

        and: "mocked currentTenant bean configured with tenant id 123"
        filterEnabler.currentTenant = Mock(CurrentTenant)
        1 * filterEnabler.currentTenant.get() >> 123

        and: "a mocked Hibernate filter"
        Filter mockedFilter = Mock()

        when: "we notice the bean about the new Hibernate session"
        filterEnabler.newHibernateSessionCreated(newSessionEvent)

        then: "enableFilter should be invoked on the session we passed in with the event"
        1 * mockedSession.enableFilter("multiTenantHibernateFilter") >> mockedFilter

        and: "the filter should be configured with the current tenant id"
        1 * mockedFilter.setParameter("tenantId", 123)
    }

    def "the Hibernate filter should be disabled if current tenant id is null"() {
        given: "a mocked Hibernate session and a new session event"
        Session mockedSession = Mock()
        Event newSessionEvent = new BaseEvent("hibernate.sessionCreated", mockedSession)

        and: "mocked currentTenant bean not configured with a tenant id"
        filterEnabler.currentTenant = Mock(CurrentTenant)
        1 * filterEnabler.currentTenant.get() >> null

        when: "we notice the bean about the new Hibernate session"
        filterEnabler.newHibernateSessionCreated(newSessionEvent)

        then: "disableFilter should be invoked on the session we passed in with the event"
        1 * mockedSession.disableFilter("multiTenantHibernateFilter")
    }
}
