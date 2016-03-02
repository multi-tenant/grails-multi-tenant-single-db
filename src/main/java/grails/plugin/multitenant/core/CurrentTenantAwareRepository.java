package grails.plugin.multitenant.core;

/**
 * Current tenant aware repository
 * @author Kim A. Betti
 */
public interface CurrentTenantAwareRepository extends TenantRepository, CurrentTenantAware {

    Tenant getCurrentTenant();

}