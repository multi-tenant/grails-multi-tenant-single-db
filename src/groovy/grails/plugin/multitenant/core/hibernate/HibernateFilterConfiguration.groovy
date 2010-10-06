package grails.plugin.multitenant.core.hibernate

import grails.plugin.multitenant.core.TenantUtils
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsAnnotationConfiguration
import org.codehaus.groovy.grails.orm.hibernate.cfg.DefaultGrailsDomainConfiguration
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsClass
import org.hibernate.engine.FilterDefinition
import org.hibernate.MappingException
import org.hibernate.Hibernate

/**
 * Defines the Hibernate filter
 */
class HibernateFilterConfiguration extends GrailsAnnotationConfiguration {
	
	public static final String TENANT_FILTER_NAME = "onlyCurrentTenant"
	public static final String TENANT_ID_PARAM_NAME = "tenantIdParam"
    public static final String TENANT_ID_FIELD_NAME = "tenantId"

    private static final String FILTER_CONDITION = ":tenantIdParam = tenant_id" //String.format(":%s = tenantId", TENANT_ID_PARAM_NAME)
    
    private GrailsApplication grailsApplication
	private boolean configLocked = false

    public void setGrailsApplication(GrailsApplication grailsApplication) {
        super.setGrailsApplication(grailsApplication)
        this.grailsApplication = grailsApplication
    }

	@Override
    protected void secondPassCompile() throws MappingException {
		if (!configLocked) {
	        super.secondPassCompile() // "Overrides the default behavior to including binding of Grails domain classes"
            addFilterDefinition()
	        enrichMultiTenantDomainClasses()
			configLocked = true
		}
    }
	
	private void enrichMultiTenantDomainClasses() {
		findMultiTenantDomainClasses().each { GrailsClass domainClass ->
			addDomainFilter(domainClass)
			addTenantIdConstraints(domainClass)
		}
	}
	
	private List findMultiTenantDomainClasses() {
		return grailsApplication.domainClasses.findAll { GrailsClass domainClass ->
			TenantUtils.hasMultiTenantAnnotation(domainClass)
		}
	}
    
    private void addFilterDefinition() {
        final Map filterParams = [:]
        filterParams.put(TENANT_ID_PARAM_NAME, Hibernate.INTEGER)
//        final filterParams = [tenantIdParam: Hibernate.INTEGER]
        
        FilterDefinition filterDefinition = new FilterDefinition(TENANT_FILTER_NAME, FILTER_CONDITION, filterParams)
        addFilterDefinition(filterDefinition)
    }

    private void addDomainFilter(DefaultGrailsDomainClass domainClass) {
        def entity = getClassMapping(domainClass.fullName)
        entity.addFilter(TENANT_FILTER_NAME, FILTER_CONDITION);
    }

    private void addTenantIdConstraints(DefaultGrailsDomainClass domainClass) {
        (domainClass.constraints?.get(TENANT_ID_FIELD_NAME)
            ?.applyConstraint(ConstrainedProperty.NULLABLE_CONSTRAINT, false))
    }

}