package grails.plugin.multitenant.core;

import grails.plugin.multitenant.core.annotation.MultiTenant;
import grails.plugin.multitenant.core.hibernate.HibernateFilterConfiguration;
import groovy.lang.Closure;

import java.lang.annotation.Annotation;

import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TenantUtils {
	
	private final static Logger log = LoggerFactory.getLogger(TenantUtils.class);

	private static CurrentTenant currentTenant;
	private static SessionFactory sessionFactory;
	
	public static boolean hasMultiTenantAnnotation(DefaultGrailsDomainClass domainClass) {
		return hasMultiTenantAnnotation(domainClass.getClazz());
	}
	
	public static boolean hasMultiTenantAnnotation(Class clazz) {
	    Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations)
            if (annotation instanceof MultiTenant)
                return true;
        
        return false;
	}
	
	public static void enableHibernateFilter(Session session, int tenantId) {
		session.enableFilter(HibernateFilterConfiguration.TENANT_FILTER_NAME)
        	.setParameter(HibernateFilterConfiguration.TENANT_ID_PARAM_NAME, tenantId);
	}
	
	public void withTenantId(Integer temporaryTenantId, Closure closure) {
        int previousId = currentTenant.get();
        Session currentSession = sessionFactory.getCurrentSession();
        try {
            enableHibernateFilter(currentSession, temporaryTenantId);
            currentTenant.set(temporaryTenantId);
            closure.call();
        } finally {
            enableHibernateFilter(currentSession, previousId);
            currentTenant.set(previousId);
        }
    }
	
	public void setCurrentTenant(CurrentTenant currentTenant) {
		TenantUtils.currentTenant = currentTenant;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		TenantUtils.sessionFactory = sessionFactory;
	}
	
}