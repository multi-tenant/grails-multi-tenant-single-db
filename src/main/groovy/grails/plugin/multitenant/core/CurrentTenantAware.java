package grails.plugin.multitenant.core;

/**
 * Beans implementing this interface will
 * get the 'currentTenant' bean injected.
 * @author Kim A. Betti
 */
public interface CurrentTenantAware {

    void setCurrentTenant(CurrentTenant currentTenant);

}
