package grails.plugin.multitenant.core.impl;

import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsClass;
import org.codehaus.groovy.grails.plugins.support.aware.GrailsApplicationAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import grails.plugin.multitenant.core.Tenant;
import grails.plugin.multitenant.core.TenantRepository;
import grails.plugin.multitenant.core.exception.TenantException;

/**
 * Dummy implementation of TenantRepository used when
 * the application doesn't provide an implementation.
 * 
 * @author Kim A. Betti
 */
public class DefaultTenantRepository implements TenantRepository, GrailsApplicationAware {

    private static final Logger log = LoggerFactory.getLogger(DefaultTenantRepository.class);

    private Class<? extends Tenant> tenantClass;

    @Override
    public void setGrailsApplication(GrailsApplication grailsApplication) {
        if (tenantClass == null) {
            tenantClass = scanForTenantClass(grailsApplication);
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Tenant> scanForTenantClass(GrailsApplication grailsApplication) {
        log.debug("Scanning Grails domain classes for one implementing Tenant");
        Class<? extends Tenant> tenantDomainClass = null;
        for (GrailsClass grailsClass : grailsApplication.getArtefacts("Domain")) {
            Class<?> realDomainClass = grailsClass.getClazz();
            if (Tenant.class.isAssignableFrom(realDomainClass)) {
                if (tenantDomainClass != null) {
                    throw new TenantException(String.format("Found multiple implementations of the Tenant interface (%s and %s)",
                            tenantDomainClass.getName(), realDomainClass.getName()));
                }

                tenantDomainClass = (Class<? extends Tenant>) realDomainClass;
            }
        }

        log.info("Using {} as tenant class", tenantDomainClass.getCanonicalName());
        return tenantDomainClass;
    }

    @Override
    public void setTenantClass(Class<? extends Tenant> tenantClass) {
        this.tenantClass = tenantClass;
    }

    @Override
    public Class<? extends Tenant> getTenantClass() {
        if (tenantClass == null) {
            throw new TenantException("DefaultTenantRepository has tried, but not been able to "
                    + "locate your implementation of the Tenant interface. "
                    + "Consider creating a tenantRepository bean or re-define "
                    + "DefaultTenantRepository with your tenant class.");
        } else {
            return tenantClass;
        }
    }

    @Override
    public Tenant findByTenantId(Integer tenantId) {
        throw new TenantException("Operation not available on default tenant repository. "
                + "You should provide your own implementation of the TenantRepository interface.");
    }

}
