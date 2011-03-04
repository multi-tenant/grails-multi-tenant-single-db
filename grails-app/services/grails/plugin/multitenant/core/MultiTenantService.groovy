package grails.plugin.multitenant.core

import grails.plugin.hibernatehijacker.template.HibernateTemplates;

import static org.springframework.transaction.TransactionDefinition.*

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
    def doWithTenantId(Integer tenantId, Closure callback) {
        Integer oldTenantId = currentTenant.get()
        hibernateTemplates.withNewSession { 
            hibernateTemplates.withTransaction(PROPAGATION_REQUIRES_NEW) {
                try {
                    currentTenant.set(tenantId)
                    callback.call()
                } finally {
                    currentTenant.set(oldTenantId)
                }
            }    
        }
    }
    
}