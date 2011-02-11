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
import org.springframework.beans.factory.InitializingBean;

/**
 * Subscribes itself to hibernate.sessionCreated events.
 * Enables the tenant Hibernate filter with the current tenant id (if any)
 * 
 * @author Kim A. Betti
 */
@HawkEventConsumer
public class TenantHibernateFilterEnabler implements InitializingBean {

    private static Log log = LogFactory.getLog(TenantHibernateFilterEnabler.class);

    private SessionFactory sessionFactory;
    private CurrentTenant currentTenant;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug("Subscribing to hibernate.sessionCreated");
    }

    @Consuming("hibernate.sessionCreated")
    public void newHibernateSessionCreated(Event event) {
        Session newSession = (Session) event.getPayload();
        Integer currentTenantId = currentTenant.get();
        enableHibernateFilter(newSession, currentTenantId);
    }

    @Consuming(CurrentTenant.TENANT_CHANGE_EVENT)
    public void currentTenantUpdated(Event event) {
        Integer updatedTenantId = (Integer) event.getPayload();
        Session currentSession = sessionFactory.getCurrentSession();
        enableHibernateFilter(currentSession, updatedTenantId);
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
