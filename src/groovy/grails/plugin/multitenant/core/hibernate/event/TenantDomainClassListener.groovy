package grails.plugin.multitenant.core.hibernate.event

import grails.plugin.multitenant.core.Tenant
import grails.plugins.hawkeventing.Event
import grails.util.GrailsNameUtils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean

/**
 * Finds the Tenant class and adds Hibernate event listeners. 
 * Works as proxy translating Hibernate events to non-Hibernate
 * specific events. This class will have to be moved if we want
 * to make a -core plugin completely independent of Hibernate.  
 * 
 * @author Kim A. Betti
 */
class TenantDomainClassListener implements InitializingBean {

    private final static Logger log = LoggerFactory.getLogger(TenantDomainClassListener);

    def eventBroker
    def multiTenantContext

    public void afterPropertiesSet() {
        Class<? extends Tenant> tenantDomainClass = multiTenantContext.getTenantClass()
        if (!tenantDomainClass) {
            log.warn "Unable to find any domain classes implementing the Tenant interface, tenant events will not be published."
        } else {
            log.info "Registering tenant event listeners for " + tenantDomainClass.simpleName
            registerTenantListeners(tenantDomainClass)
        }
    }

    private void registerTenantListeners(Class<? extends Tenant> tenantDomainClass) {
        String tenantClassPropertyName = GrailsNameUtils.getPropertyName(tenantDomainClass)
        registerForPostInsertEvents tenantClassPropertyName
        registerForPostDeleteEvents tenantClassPropertyName
        registerForPostUpdateEvents tenantClassPropertyName
    }

    private void registerForPostInsertEvents(String tenantClassPropertyName) {
        eventBroker.subscribe("hibernate.postInsert." + tenantClassPropertyName) { Event event ->
            eventBroker.publish("tenant.created", event.payload)
        }
    }

    private void registerForPostDeleteEvents(String tenantClassPropertyName) {
        eventBroker.subscribe("hibernate.postDelete." + tenantClassPropertyName) { Event event ->
            eventBroker.publish("tenant.deleted", event.payload)
        }
    }

    private void registerForPostUpdateEvents(String tenantClassPropertyName) {
        eventBroker.subscribe("hibernate.postUpdate." + tenantClassPropertyName) { Event event ->
            eventBroker.publish("tenant.updated", event.payload)
        }
    }
    
}