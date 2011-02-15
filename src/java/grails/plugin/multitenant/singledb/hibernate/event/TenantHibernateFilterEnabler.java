package grails.plugin.multitenant.singledb.hibernate.event;

import grails.plugin.multitenant.core.CurrentTenant;
import grails.plugin.multitenant.singledb.hibernate.TenantFilterCfg;
import grails.plugins.hawkeventing.Event;
import grails.plugins.hawkeventing.annotation.Consuming;
import grails.plugins.hawkeventing.annotation.HawkEventConsumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Subscribes itself to hibernate.sessionCreated events.
 * Enables the tenant Hibernate filter with the current tenant id (if any)
 * 
 * @author Kim A. Betti
 */
@HawkEventConsumer
public class TenantHibernateFilterEnabler {

    private static Log log = LogFactory.getLog(TenantHibernateFilterEnabler.class);

    private SessionFactory sessionFactory;
    private CurrentTenant currentTenant;

    @Consuming("hibernate.sessionCreated")
    public void newHibernateSessionCreated(Event event) {
        Session newSession = (Session) event.getPayload();
        Integer currentTenantId = currentTenant.get();
        enableHibernateFilter(newSession, currentTenantId);
    }

    @Consuming(CurrentTenant.TENANT_AFTER_CHANGE_EVENT)
    public void currentTenantUpdated(Event event) {
        if (hasSessionBoundToThread()) {
            Integer updatedTenantId = (Integer) event.getPayload();
            Session currentSession = sessionFactory.getCurrentSession();
            enableHibernateFilter(currentSession, updatedTenantId);
        }
    }

    private boolean hasSessionBoundToThread() {
        return TransactionSynchronizationManager.hasResource(sessionFactory);
    }

    public void enableHibernateFilter(Session session, Integer tenantId) {
        session.enableFilter(TenantFilterCfg.TENANT_FILTER_NAME).setParameter (
                TenantFilterCfg.TENANT_ID_PARAM_NAME, tenantId);
    }

    public void setCurrentTenant(CurrentTenant currentTenant) {
        this.currentTenant = currentTenant;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

}
