package grails.plugin.multitenant.core;

/**
 * Each tenant is identified by a number.
 * The only / default implementation of this interface uses
 * a thread local storage for this number.
 */
public interface CurrentTenant {

    String TENANT_BEFORE_CHANGE_EVENT = "multitenant.set-current-tenant.before";
    String TENANT_AFTER_CHANGE_EVENT = "multitenant.set-current-tenant.after";

    Integer get();

    void set(Integer tenantId);

    boolean isSet();

}