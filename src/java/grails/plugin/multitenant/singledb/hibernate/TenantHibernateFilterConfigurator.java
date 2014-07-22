package grails.plugin.multitenant.singledb.hibernate;

import grails.plugin.hibernatehijacker.hibernate.HibernateConfigPostProcessor;
import grails.plugin.multitenant.core.MultiTenantDomainClass;

import java.util.Iterator;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.mapping.PersistentClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the Hibernate filter.
 *
 * The Hibernate Hijacker detects beans implementing
 * HibernateConfigPostProcessor and will make sure that the Configuration
 * instances is passed through this instance when the application is starting.
 *
 * @author Kim A. Betti <kim@developer-b.com>
 */
public class TenantHibernateFilterConfigurator implements HibernateConfigPostProcessor {

    private static Logger log = LoggerFactory.getLogger(TenantHibernateFilterConfigurator.class);

    private FilterDefinition multiTenantHibernateFilter;

    @Override
    public void doPostProcessing(Configuration configuration) throws HibernateException {
        log.debug("Configuring multi-tenant Hibernate filter");
        addMultiTenantFilterDefinition(configuration);
        enrichMultiTenantDomainClasses(configuration);
    }

    private void addMultiTenantFilterDefinition(Configuration configuration) {
        log.debug("Defining Multi Tenant Hibernate filer");
        configuration.addFilterDefinition(multiTenantHibernateFilter);
    }

    private void enrichMultiTenantDomainClasses(Configuration configuration) {
        Iterator<PersistentClass> mappingIterator = configuration.getClassMappings();
        while (mappingIterator.hasNext()) {
            PersistentClass persistentClass = mappingIterator.next();
            if (isMultiTenantClass(persistentClass.getMappedClass())) {
                enrichMultiTenantDomainClass(persistentClass);
            }
        }
    }

    private void enrichMultiTenantDomainClass(PersistentClass persistentClass) {
        log.trace("Enabling multi-tenant mode for domain class {}", persistentClass.getClassName());
        addDomainFilter(persistentClass);
    }

    private boolean isMultiTenantClass(Class<?> mappedClass) {
        return MultiTenantDomainClass.class.isAssignableFrom(mappedClass);
    }

    private void addDomainFilter(PersistentClass persistentClass) {
        String filterName = multiTenantHibernateFilter.getFilterName();
        String condition = multiTenantHibernateFilter.getDefaultFilterCondition();
        persistentClass.addFilter(filterName, condition);
    }

    public void setMultiTenantHibernateFilter(FilterDefinition multiTenantHibernateFilter) {
        this.multiTenantHibernateFilter = multiTenantHibernateFilter;
    }

}