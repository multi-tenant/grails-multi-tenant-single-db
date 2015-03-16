package demo

import grails.plugin.multitenant.core.CurrentTenant
import grails.plugin.multitenant.core.CurrentTenantAware

/**
 * @author Steve Ronderos
 */
class DemoStore implements CurrentTenantAware {

    CurrentTenant currentTenant

    @Override
    void setCurrentTenant(CurrentTenant currentTenant) {
        this.currentTenant = currentTenant
    }
}
