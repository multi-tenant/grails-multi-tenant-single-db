package grails.plugin.multitenant.core.util;

import grails.plugin.multitenant.core.CurrentTenant;
import grails.plugin.multitenant.core.annotation.MultiTenant;
import grails.plugin.multitenant.singledb.hibernate.TenantFilterCfg;
import groovy.lang.Closure;

import java.lang.annotation.Annotation;

import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass;
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

    public static Class<?> TENANT_DOMAIN_CLASS;

    public static boolean hasMultiTenantAnnotation(
            DefaultGrailsDomainClass domainClass) {
        return hasMultiTenantAnnotation(domainClass.getClazz());
    }

    public static boolean hasMultiTenantAnnotation(Class<?> clazz) {
        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof MultiTenant) {
                return true;
            }
        }

        return false;
    }

    public static boolean isMultiTenantClass(Class<?> clazz) {
        if (hasMultiTenantAnnotation(clazz)) {
            return true;
        } else {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null && superclass != Object.class) {
                return isMultiTenantClass(superclass);
            }
        }

        return false;
    }

    public void withTenantId(Integer temporaryTenantId, Closure closure) {
        Integer previousTenantId = currentTenant.get();
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.flush();

        try {
            enableHibernateFilter(currentSession, temporaryTenantId);
            currentTenant.set(temporaryTenantId);
            closure.call();
            currentSession.flush(); // Force events to happen with the expected id
        } finally {
            enableHibernateFilter(currentSession, previousTenantId);
            currentTenant.set(previousTenantId);
        }
    }

    public static void enableHibernateFilter(Session session, Integer tenantId) {
        session.enableFilter(TenantFilterCfg.TENANT_FILTER_NAME).setParameter (
                TenantFilterCfg.TENANT_ID_PARAM_NAME, tenantId);
    }

    public void setCurrentTenant(CurrentTenant currentTenant) {
        TenantUtils.currentTenant = currentTenant;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        TenantUtils.sessionFactory = sessionFactory;
    }

}