package grails.plugin.multitenant.core.hibernate.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.InitializingBean;

import grails.plugin.eventing.EventBroker;
import grails.plugin.eventing.EventConsumer;
import grails.plugin.multitenant.core.CurrentTenant;
import grails.plugin.multitenant.core.util.TenantUtils;

/**
 * 
 * @author Kim A. Betti
 */
public class TenantHibernateFilterEnabler implements EventConsumer, InitializingBean {
    
    private static Log log = LogFactory.getLog(TenantHibernateFilterEnabler.class);

    private CurrentTenant currentTenant;
    private EventBroker eventBroker;
    
    @Override
    public void consume(Object event, EventBroker broker) {
        Session session = (Session) event;
        Integer currentTenantId = currentTenant.get();
        if (currentTenantId >= 0)
            TenantUtils.enableHibernateFilter(session, currentTenantId);
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug("Subscribing to hibernate.sessionCreated");
        eventBroker.subscribe("hibernate.sessionCreated", this);
    }
    
    public void setCurrentTenant(CurrentTenant currentTenant) {
        this.currentTenant = currentTenant;
    }
    
    public void setEventBroker(EventBroker eventBroker) {
        this.eventBroker = eventBroker;
    }

    @Override
    public String getName() {
        return "Hibernate tenant filter activator";
    }

}
