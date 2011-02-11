package grails.plugin.multitenant.singledb.hibernate

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import grails.util.GrailsNameUtils;

import grails.plugins.hawkeventing.EventBroker;
import grails.plugins.hawkeventing.EventConsumer;

import grails.plugin.multitenant.singledb.util.TenantUtils
import grails.plugin.multitenant.singledb.event.*;

import grails.plugin.hibernatehijacker.hibernate.HibernateConfigPostProcessor
import grails.plugin.multitenant.singledb.hibernate.event.TenantHibernateEventListener
import org.codehaus.groovy.grails.orm.hibernate.validation.UniqueConstraint;

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.codehaus.groovy.grails.validation.Constraint;
import org.codehaus.groovy.grails.commons.*
import org.hibernate.engine.FilterDefinition
import org.hibernate.Hibernate

/**
 * Defines the Hibernate filter.
 * 
 * The Hibernate Hijacker detects beans implementing HibernateConfigPostProcessor
 * and will make sure that the Configuration instances is passed through this instance
 * when the application is starting.
 * 
 * @author Kim A. Betti <kim@developer-b.com>
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
		log.info "Defining Hibernate filer: " + TenantFilterCfg.TENANT_FILTER_NAME
        final Map filterParams = new HashMap();
        filterParams.put(TenantFilterCfg.TENANT_ID_PARAM_NAME, Hibernate.INTEGER)
        FilterDefinition filterDefinition = new FilterDefinition (
            TenantFilterCfg.TENANT_FILTER_NAME, TenantFilterCfg.FILTER_CONDITION, filterParams)
        
        configuration.addFilterDefinition(filterDefinition)
    }
    
	private void enrichMultiTenantDomainClasses(Configuration configuration) {
		def classNames = []
		multiTenantDomainClasses.each { GrailsClass domainClass ->
			classNames << domainClass.clazz.simpleName
			addDomainFilter(domainClass, configuration)
			addTenantIdConstraints(domainClass)
			//fixUniqueConstraints(domainClass)
		}
				
		log.debug "Added multi tenant functionality to: " + classNames.sort()
	}
    
	private List<GrailsClass> getMultiTenantDomainClasses() {
		grailsApplication.domainClasses.findAll { GrailsClass domainClass ->
			TenantUtils.isMultiTenantClass(domainClass.getClazz())
		}
	}

    private void addDomainFilter(DefaultGrailsDomainClass domainClass, Configuration configuration) {
        def entity = configuration.getClassMapping(domainClass.fullName)
        entity.addFilter(TenantFilterCfg.TENANT_FILTER_NAME, TenantFilterCfg.FILTER_CONDITION);
    }

	/**
	 * Can inject unique: 'tenantId' in all fields with a unique constraint,
	 * the problem is this might not be what we want to do in all cases. 
	 * @param domainClass
	 */
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
