package grails.plugin.multitenant.core

import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW
import grails.plugin.hibernatehijacker.template.HibernateTemplates

/**
 * Allows for temporary manipulation of the current tenant.
 * Note: If you use this too often you're probably doing something wrong.
 * @author Kim A. Betti
 */
class MultiTenantService {

    static transactional = false

    CurrentTenant currentTenant
    HibernateTemplates hibernateTemplates

    /**
     * Execute some code in the 'namespace' of the given tenant id.
     * The code will be executed in a new transaction with a new session
     * to avoid other tenants entities laying in the first level cache to leak in.
     */
    def doWithTenantId(Long tenantId, Closure callback) {
        Long oldTenantId = currentTenant.get()
        try {
            if(log.debugEnabled) log.debug "doWithTenantId oldTenantId - $oldTenantId"
            currentTenant.set(tenantId)
            if(log.debugEnabled) log.debug "doWithTenantId runin with tenant - $tenantId"

            hibernateTemplates.withNewSession {
                hibernateTemplates.withTransaction(PROPAGATION_REQUIRES_NEW) {
                    callback.call()
                }
            }
        }
        finally{
            currentTenant.set(oldTenantId)
        }
    }
}
