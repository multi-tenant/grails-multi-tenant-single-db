package grails.plugin.multitenant.core

/**
 * 
 * @author Kim A. Betti
 */
class MultiTenantService {

    static transactional = false

    CurrentTenant currentTenant
    
    void doWithTenantId(Integer tenantId, Closure closure) {
        Integer oldTenantId = currentTenant.get()
        try {
            currentTenant.set(tenantId)
            closure.call()
        } finally {
            currentTenant.set(oldTenantId)
        }
    }

}