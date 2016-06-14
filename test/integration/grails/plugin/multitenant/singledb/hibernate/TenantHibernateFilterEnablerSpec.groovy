package grails.plugin.multitenant.singledb.hibernate

import grails.plugins.hawkeventing.BaseEvent
import grails.plugins.hawkeventing.Event
import grails.test.spock.IntegrationSpec
import org.hibernate.Filter
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.engine.spi.FilterDefinition
import org.hibernate.internal.FilterImpl
import spock.lang.Unroll

class TenantHibernateFilterEnablerSpec extends IntegrationSpec {

    FilterDefinition multiTenantHibernateFilter
    TenantHibernateFilterEnabler tenantHibernateFilterEnabler
    SessionFactory sessionFactory

    def "a null tenant id disables the filter"() {
        given:
        Event updatedTenantEvent = new BaseEvent("multitenant.set-current-tenant.after", null)

        when:
        tenantHibernateFilterEnabler.currentTenantUpdated(updatedTenantEvent)

        then:
        Session session = sessionFactory.getCurrentSession()
        String multiTenantFilterName = multiTenantHibernateFilter.filterName
        Filter enabledFilter = session.getEnabledFilter(multiTenantFilterName)
        enabledFilter == null
    }

    @Unroll("Enabling multi-tenant Hibernate filter with tenant id #tenantId")
    def "negative and positive numbers makes it to the filter"() {
        when:
        Event updatedTenantEvent = new BaseEvent("multitenant.set-current-tenant.after", tenantId)
        tenantHibernateFilterEnabler.currentTenantUpdated(updatedTenantEvent)

        then:
        Session session = sessionFactory.getCurrentSession()
        String multiTenantFilterName = multiTenantHibernateFilter.filterName
        FilterImpl enabledFilter = session.getEnabledFilter(multiTenantFilterName) as FilterImpl

        and:
        enabledFilter.getParameter("tenantId") == tenantId

        where:
        tenantId << [-1, 0, 1]
    }
}
