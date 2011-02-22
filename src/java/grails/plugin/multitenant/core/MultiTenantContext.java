package grails.plugin.multitenant.core;

import grails.plugin.multitenant.core.annotation.MultiTenant;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsClass;
import org.codehaus.groovy.grails.commons.GrailsDomainClass;
import org.springframework.beans.factory.InitializingBean;

/**
 * This class was created to get rid of TenantUtils and make some
 * other classes independent of grailsApplication.
 * 
 * @author Kim A. Betti
 */
public class MultiTenantContext implements InitializingBean {

    private GrailsApplication grailsApplication;

    private List<GrailsDomainClass> multiTenantDomainClasses;

    private Class<? extends Tenant> tenantClass;

    @Override
    public void afterPropertiesSet() {
        this.multiTenantDomainClasses = findMultiTenantDomainClasses();
        this.tenantClass = findTenantClass();
    }

    public List<GrailsDomainClass> findMultiTenantDomainClasses() {
        List<GrailsDomainClass> domainClasses = new ArrayList<GrailsDomainClass>();
        for (GrailsClass grailsClass : grailsApplication.getArtefacts("Domain")) {
            Class<?> realClass = grailsClass.getClazz();
            if (hasMultiTenantAnnotation(realClass)) {
                domainClasses.add((GrailsDomainClass)grailsClass);
            }
        }

        return domainClasses;
    }

    public boolean hasMultiTenantAnnotation(Class<?> clazz) {
        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof MultiTenant) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Tenant> findTenantClass() {
        for (GrailsClass grailsClass : grailsApplication.getArtefacts("Domain")) {
            Class<?> realDomainClass = grailsClass.getClazz();
            if (Tenant.class.isAssignableFrom(realDomainClass)) {
                return (Class<? extends Tenant>) realDomainClass;
            }
        }

        return null;
    }

    public List<GrailsDomainClass> getMultiTenantDomainClasses() {
        return multiTenantDomainClasses;
    }

    public Class<? extends Tenant> getTenantClass() {
        return this.tenantClass;
    }

    public boolean isMultiTenantDomainClass(Class<?> clazz) {
        for (GrailsDomainClass domainClass : multiTenantDomainClasses) {
            if (domainClass.getClazz() == clazz) {
                return true;
            }
        }

        return false;
    }

    public void setGrailsApplication(GrailsApplication grailsApplication) {
        this.grailsApplication = grailsApplication;
    }

}
