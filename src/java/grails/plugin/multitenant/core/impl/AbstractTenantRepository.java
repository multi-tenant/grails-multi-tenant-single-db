package grails.plugin.multitenant.core.impl;

import grails.plugin.multitenant.core.Tenant;
import grails.plugin.multitenant.core.TenantRepository;

/**
 * 
 * @author Kim A. Betti
 */
public abstract class AbstractTenantRepository implements TenantRepository {

    protected Class<? extends Tenant> tenantClass;

    @Override
    public Class<? extends Tenant> getTenantClass() {
        return tenantClass;
    }

    @Override
    public void setTenantClass(Class<? extends Tenant> tenantClass) {
        this.tenantClass = tenantClass;
    }

}