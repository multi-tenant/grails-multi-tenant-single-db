package grails.plugin.multitenant.core.hibernate.event

import java.lang.annotation.Annotation;

import grails.plugin.multitenant.core.annotation.TenantDomainClass;
import grails.plugin.multitenant.core.util.TenantUtils;
import grails.util.GrailsNameUtils;

import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

class TenantDomainClassListener {
	
	private final static Logger log = LoggerFactory.getLogger(TenantDomainClassListener);

	def eventBroker
	
	public void setGrailsApplication(GrailsApplication grailsApplication) {
		Class<?> tenantDomainClass = getTenantDomainClass(grailsApplication);
		if (!tenantDomainClass) {
			log.warn "Unable to find any domain classes annotated with @TenantDomainClass, tenant events will not be published."
		} else {
			log.info "Registering tenant event listeners for " + tenantDomainClass.simpleName
			registerTenantListeners(tenantDomainClass)
		}
	}
	
	private Class<?> getTenantDomainClass(GrailsApplication grailsApplication) {
		GrailsClass[] artefacts = grailsApplication.getArtefacts("Domain");
		for (GrailsClass artefact : artefacts) {
			Annotation[] annotations = artefact.getClazz().getAnnotations();
			for (Annotation annotation : annotations)
				if (annotation instanceof TenantDomainClass)
					return artefact.getClazz();
		}

		log.debug("Unable to find any domain classes annotated with @TenantDomainClass");
		return null;
	}
	
	
	private void registerTenantListeners(Class<?> tenantDomainClass) {
		String tenantClassPropertyName = GrailsNameUtils.getPropertyName(tenantDomainClass)
		
		eventBroker.subscribe("hibernate.postInsert." + tenantClassPropertyName) { event ->
			eventBroker.publish("tenant.created", event.entity)
		}
		
		eventBroker.subscribe("hibernate.postDelete." + tenantClassPropertyName) { event ->
			eventBroker.publish("tenant.deleted", event.entity)
		}
		
		eventBroker.subscribe("hibernate.postUpdate." + tenantClassPropertyName) { event ->
			eventBroker.publish("tenant.updated", event.entity)
		}
		
	}
	
}