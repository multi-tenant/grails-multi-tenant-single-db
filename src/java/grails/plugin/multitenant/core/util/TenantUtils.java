package grails.plugin.multitenant.core.util;

import grails.plugin.multitenant.core.CurrentTenant;
import groovy.lang.Closure;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;

/**
 * @deprecated
 * @author Kim A. Betti <kim@developer-b.com>
 */
@Deprecated
public class TenantUtils {

    private static CurrentTenant currentTenant;
    private static SessionFactory sessionFactory;

    public void withTenantId(Integer temporaryTenantId, Closure closure) {
        Integer previousTenantId = currentTenant.get();
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.flush();

        try {
            currentTenant.set(temporaryTenantId);
            closure.call();
            currentSession.flush(); // Force events to happen with the expected id
        } finally {
            currentTenant.set(previousTenantId);
        }
    }

    public void setCurrentTenant(CurrentTenant currentTenant) {
        TenantUtils.currentTenant = currentTenant;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        TenantUtils.sessionFactory = sessionFactory;
    }

}