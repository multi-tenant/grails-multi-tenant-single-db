package grails.plugin.multitenant.singledb.hibernate;

import grails.plugin.multitenant.core.Tenant;
import grails.plugins.hawkeventing.Event;
import grails.plugins.hawkeventing.EventBroker;
import grails.plugins.hawkeventing.EventConsumer;
import grails.util.GrailsNameUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Finds the Tenant class and adds Hibernate event listeners.
 * Works as proxy translating Hibernate events to non-Hibernate
 * specific events. This class will have to be moved if we want
 * to make a -core plugin completely independent of Hibernate.
 * 
 * @author Kim A. Betti
 */
public class TenantHibernateEventProxy implements InitializingBean {

    private final static Logger log = LoggerFactory.getLogger(TenantHibernateEventProxy.class);

    private EventBroker eventBroker;
    private Class<? extends Tenant> tenantClass;

    @Override
    public void afterPropertiesSet() {
        if (tenantClass != null) {
            log.info("Registering tenant event listeners for {}", tenantClass.getSimpleName());
            registerTenantListeners(tenantClass);
        } else {
            log.warn("Tenant class not set. Add something like this to Config.groovy: multitenant.tenantClass = com.your-app.Customer");
        }
    }

    public void registerTenantListeners(Class<? extends Tenant> tenantDomainClass) {
        log.debug("Subscribing to Hibernate events from {}", tenantDomainClass.getSimpleName());
        String tenantClassPropertyName = GrailsNameUtils.getPropertyName(tenantDomainClass);
        registerForPostInsertEvents(tenantClassPropertyName);
        registerForPostDeleteEvents(tenantClassPropertyName);
        registerForPostUpdateEvents(tenantClassPropertyName);
    }

    private void registerForPostInsertEvents(String tenantClassPropertyName) {
        EventConsumer proxy = new HawkEventProxy(eventBroker, "tenant.created");
        eventBroker.subscribe("hibernate.postInsert." + tenantClassPropertyName, proxy);
    }

    private void registerForPostDeleteEvents(String tenantClassPropertyName) {
        EventConsumer proxy = new HawkEventProxy(eventBroker, "tenant.deleted");
        eventBroker.subscribe("hibernate.postDelete." + tenantClassPropertyName, proxy);
    }

    private void registerForPostUpdateEvents(String tenantClassPropertyName) {
        EventConsumer proxy = new HawkEventProxy(eventBroker, "tenant.updated");
        eventBroker.subscribe("hibernate.postUpdate." + tenantClassPropertyName, proxy);
    }

    public void setEventBroker(EventBroker eventBroker) {
        this.eventBroker = eventBroker;
    }

    public void setTenantClass(Class<? extends Tenant> tenantClass) {
        this.tenantClass = tenantClass;
    }

}

/**
 * Re-publishes events under a new name.
 */
class HawkEventProxy implements EventConsumer {

    EventBroker eventBroker;
    String eventName;

    public HawkEventProxy(EventBroker eventBroker, String eventName) {
        this.eventBroker = eventBroker;
        this.eventName = eventName;
    }

    @Override
    public void consume(Event event) {
        eventBroker.publish(eventName, event.getPayload());
    }

}