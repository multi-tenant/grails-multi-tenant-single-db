package grails.plugin.multitenant.singledb.hibernate;

import grails.plugin.hibernatehijacker.hibernate.HibernateConfigPostProcessor;
import grails.plugin.hibernatehijacker.hibernate.events.HibernateEventUtil;
import grails.plugin.multitenant.core.MultiTenantContext;
import grails.plugin.multitenant.core.hibernate.event.TenantHibernateEventListener;

import java.util.List;
import java.util.Map;

import org.codehaus.groovy.grails.commons.GrailsDomainClass;
import org.codehaus.groovy.grails.validation.ConstrainedProperty;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.event.EventListeners;
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

    // TODO: Move this somewhere else..? (also used in AST).
    public final static String TENANT_ID_FIELD_NAME = "tenantId";

    private FilterDefinition multiTenantHibernateFilter;
    private TenantHibernateEventListener tenantHibernateEventListener;
    private MultiTenantContext multiTenantContext;

    @Override
    public void doPostProcessing(Configuration configuration) throws HibernateException {
        log.debug("Configuring multi-tenant Hibernate filter");

        createFilterDefinition(configuration);
        enrichMultiTenantDomainClasses(configuration);
        activateTenantEventListener(configuration);
    }

    private void createFilterDefinition(Configuration configuration) {
        log.debug("Defining Multi Tenant Hibernate filer");
        configuration.addFilterDefinition(multiTenantHibernateFilter);
    }

    private void enrichMultiTenantDomainClasses(Configuration configuration) {
        List<GrailsDomainClass> multiTenantDomainClasses = multiTenantContext.getMultiTenantDomainClasses();
        for (GrailsDomainClass domainClass : multiTenantDomainClasses) {
            log.debug("Enabling multi-tenant mode for domain class {}", domainClass.getClass().getSimpleName());
            addDomainFilter(domainClass, configuration);
            addTenantIdConstraints(domainClass);
        }
    }

    private void addDomainFilter(GrailsDomainClass domainClass, Configuration configuration) {
        PersistentClass entity = configuration.getClassMapping(domainClass.getFullName());
        String filterName = multiTenantHibernateFilter.getFilterName();
        String condition = multiTenantHibernateFilter.getDefaultFilterCondition();
        entity.addFilter(filterName, condition);
    }

    @SuppressWarnings("unchecked")
    private void addTenantIdConstraints(GrailsDomainClass domainClass) {
        Map<String, ConstrainedProperty> constraints = domainClass.getConstrainedProperties();
        constraints.get(TENANT_ID_FIELD_NAME).applyConstraint(ConstrainedProperty.NULLABLE_CONSTRAINT, false);
    }

    private void activateTenantEventListener(Configuration configuration) {
        EventListeners eventListeners = configuration.getEventListeners();
        HibernateEventUtil.addListener(eventListeners, tenantHibernateEventListener);
    }

    public void setMultiTenantHibernateFilter(FilterDefinition multiTenantHibernateFilter) {
        this.multiTenantHibernateFilter = multiTenantHibernateFilter;
    }

    public void setTenantHibernateEventListener(TenantHibernateEventListener tenantHibernateEventListener) {
        this.tenantHibernateEventListener = tenantHibernateEventListener;
    }

    public void setMultiTenantContext(MultiTenantContext multiTenantContext) {
        this.multiTenantContext = multiTenantContext;
    }

}