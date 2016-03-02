package grails.plugin.multitenant.core.impl;

import grails.plugin.multitenant.core.CurrentTenant;
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
        return currentTenant.get();
    }

    @Override
    public boolean isSet() {
        return currentTenant.get() != null;
    }

    @Override
    public void set(Integer tenantId) {
        eventBroker.publish(TENANT_BEFORE_CHANGE_EVENT, tenantId);
        currentTenant.set(tenantId);
        eventBroker.publish(TENANT_AFTER_CHANGE_EVENT, tenantId);
    }

    public void setEventBroker(EventBroker broker) {
        this.eventBroker = broker;
    }

}