package grails.plugin.multitenant.core;

import grails.plugins.hawkeventing.EventBroker;

public class CurrentTenantThreadLocal implements CurrentTenant {

	private static ThreadLocal<Integer> currentTenant = new ThreadLocal<Integer>();
	private EventBroker eventBroker;
	
    public Integer get() {
        Integer ct = currentTenant.get();
        return ct != null ? ct : -1;
    }

    public void set(Integer tenantId) {
        currentTenant.set(tenantId);
        // TODO: Send a message if the id changes
    }
	
    public void setEventBroker(EventBroker broker) {
		this.eventBroker = broker;
	}
    
}