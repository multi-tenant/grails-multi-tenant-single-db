package grails.plugin.multitenant.core;

import grails.plugins.hawkeventing.EventBroker;

/**
 * Stores the current tenant id on the current thread.
 * Have a look at the link below if you want more information on ThreadLocal.
 * @see http://stackoverflow.com/questions/1490919/purpose-of-threadlocal
 */
public class CurrentTenantThreadLocal implements CurrentTenant {

    private static ThreadLocal<Integer> currentTenant = new ThreadLocal<Integer>();
    private EventBroker eventBroker;

    @Override
    public Integer get() {
        Integer ct = currentTenant.get();
        return ct != null ? ct : -1;
    }

    @Override
    public void set(Integer tenantId) {
        currentTenant.set(tenantId);
        // TODO: Send a message if the id changes
    }

    public void setEventBroker(EventBroker broker) {
        this.eventBroker = broker;
    }

}