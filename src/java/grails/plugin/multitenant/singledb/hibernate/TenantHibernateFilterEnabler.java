package grails.plugin.multitenant.singledb.hibernate;

import grails.plugin.multitenant.core.CurrentTenant;
import grails.plugins.hawkeventing.Event;
import grails.plugins.hawkeventing.annotation.Consuming;
import grails.plugins.hawkeventing.annotation.HawkEventConsumer;

import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.engine.FilterDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Subscribes itself to hibernate.sessionCreated events. Enables the tenant
 * Hibernate filter with the current tenant id (if any).
 *
 * Important: tenantId = null will disable the Hibernate filter!
 * @author Kim A. Betti
 */
@HawkEventConsumer
public class TenantHibernateFilterEnabler {

    private SessionFactory sessionFactory;
    private CurrentTenant currentTenant;
    private FilterDefinition multiTenantHibernateFilter;

    @Consuming("hibernate.sessionCreated")
    public void newHibernateSessionCreated(Event event) {
        Session newSession = (Session) event.getPayload();
        Integer currentTenantId = currentTenant.get();
        updateFilterParameter(newSession, currentTenantId);
    }

    @Consuming(CurrentTenant.TENANT_AFTER_CHANGE_EVENT)
    public void currentTenantUpdated(Event event) {
        if (hasSessionBoundToThread()) {
            Integer updatedTenantId = (Integer) event.getPayload();
            Session currentSession = sessionFactory.getCurrentSession();
            updateFilterParameter(currentSession, updatedTenantId);
        }
    }

    private boolean hasSessionBoundToThread() {
        return TransactionSynchronizationManager.hasResource(sessionFactory);
    }

    private void updateFilterParameter(Session session, Integer tenantId) {
        if (tenantId != null) {
            enableHibernateFilterForTenant(session, tenantId);
        } else {
            disableHibernateFilter(session);
        }
    }

    @SuppressWarnings("unchecked")
    private void enableHibernateFilterForTenant(Session session, Integer tenantId) {
        String filterName = multiTenantHibernateFilter.getFilterName();
        Set<String> paramNames = multiTenantHibernateFilter.getParameterNames();
        String paramName = paramNames.iterator().next();
        session.enableFilter(filterName).setParameter(paramName, tenantId);
    }

    private void disableHibernateFilter(Session session) {
        String filterName = multiTenantHibernateFilter.getFilterName();
        session.disableFilter(filterName);
    }

    public void setCurrentTenant(CurrentTenant currentTenant) {
        this.currentTenant = currentTenant;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setMultiTenantHibernateFilter(FilterDefinition multiTenantHibernateFilter) {
        this.multiTenantHibernateFilter = multiTenantHibernateFilter;
    }

}
