package grails.plugin.multitenant.core.hibernate

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import grails.util.GrailsNameUtils;
import grails.plugin.eventing.EventConsumer;
import grails.plugin.multitenant.core.util.TenantUtils
import grails.plugin.multitenant.core.event.*;
import grails.plugin.eventing.EventBroker;
import grails.plugin.hibernatehijacker.hibernate.HibernateConfigPostProcessor
import grails.plugin.multitenant.core.hibernate.event.TenantHibernateEventListener
import org.codehaus.groovy.grails.orm.hibernate.validation.UniqueConstraint;

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.codehaus.groovy.grails.validation.Constraint;
import org.codehaus.groovy.grails.commons.*
import org.hibernate.engine.FilterDefinition
import org.hibernate.Hibernate

/**
 * Defines the Hibernate filter
 */
class TenantFilterConfigurator implements HibernateConfigPostProcessor {
    
    private static Log log = LogFactory.getLog(this)
    
    TenantHibernateEventListener tenantHibernateEventListener
    GrailsApplication grailsApplication
    EventBroker eventBroker
    
    @Override
    public void doPostProcessing(Configuration configuration) throws HibernateException {
        log.debug "Configuring multi-tenant Hibernate filter"
        
        addFilterDefinition(configuration)
        enrichMultiTenantDomainClasses(configuration)
        tenantHibernateEventListener.activate(configuration)
    }
    
    private void addFilterDefinition(Configuration configuration) {
        final Map filterParams = new HashMap();
        filterParams.put(TenantFilterCfg.TENANT_ID_PARAM_NAME, Hibernate.INTEGER)
        FilterDefinition filterDefinition = new FilterDefinition (
            TenantFilterCfg.TENANT_FILTER_NAME, TenantFilterCfg.FILTER_CONDITION, filterParams)
        
        configuration.addFilterDefinition(filterDefinition)
    }
    
	private void enrichMultiTenantDomainClasses(Configuration configuration) {
		findMultiTenantDomainClasses().each { GrailsClass domainClass ->
			addDomainFilter(domainClass, configuration)
			//fixUniqueConstraints(domainClass)
			addTenantIdConstraints(domainClass)
		}
	}
    
	private List findMultiTenantDomainClasses() {
		return grailsApplication.domainClasses.findAll { GrailsClass domainClass ->
			TenantUtils.hasMultiTenantAnnotation(domainClass)
		}
	}

    private void addDomainFilter(DefaultGrailsDomainClass domainClass, Configuration configuration) {
        log.debug "Adding multi tenant Hibernate filter to: " + domainClass.getName()
        def entity = configuration.getClassMapping(domainClass.fullName)
        entity.addFilter(TenantFilterCfg.TENANT_FILTER_NAME, TenantFilterCfg.FILTER_CONDITION);
    }

	private void fixUniqueConstraints(DefaultGrailsDomainClass domainClass) {
		getUniqueConstraint(domainClass).each { UniqueConstraint constraint ->
			if (!isUniquePerTenant(constraint)) 
				constraint.setParameter(TenantFilterCfg.TENANT_ID_FIELD_NAME)
		}
	}
	
	private List getUniqueConstraint(DefaultGrailsDomainClass domainClass) {
		List uniqueConstraits = []
		domainClass.constraints?.each { String name, ConstrainedProperty prop ->
			prop.appliedConstraints.each { Constraint constraint ->
				if (constraint instanceof UniqueConstraint) {
					uniqueConstraits << constraint
				}
			}
		}
		
		return uniqueConstraits
	}
	
	private boolean isUniquePerTenant(UniqueConstraint constraint) {
		return constraint.getUniquenessGroup().contains(TenantFilterCfg.TENANT_ID_FIELD_NAME)
	}
	
    private void addTenantIdConstraints(DefaultGrailsDomainClass domainClass) {
        (domainClass.constraints?.get(TenantFilterCfg.TENANT_ID_FIELD_NAME)
            ?.applyConstraint(ConstrainedProperty.NULLABLE_CONSTRAINT, false))
    }
	
}