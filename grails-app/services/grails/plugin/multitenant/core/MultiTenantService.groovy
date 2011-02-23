package grails.plugin.multitenant.core

import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Kim A. Betti
 */
class MultiTenantService {

    static transactional = false

    CurrentTenant currentTenant
    TransactionTemplate transactionTemplate
    
    /**
     * Execute some code in the 'namespace' of the given tenant id. 
     * The code will be executed in a new transaction to avoid other tenants 
     * entities laying in the first level cache to leak in. 
     * 
     * @param tenantId
     * @param closure
     * @return
     */
    def doWithTenantId(Integer tenantId, Closure closure) {
        log.debug "Running closure with as tenant $tenantId"
        Integer oldTenantId = currentTenant.get()
        try {
            currentTenant.set(tenantId)
            TransactionCallback doWithTenantCallback = new DoWithTenantCallback(closure)
            transactionTemplate.execute doWithTenantCallback
        } finally {
            currentTenant.set(oldTenantId)
        }
    }
    
    void setTransactionManager(PlatformTransactionManager transactionManager) {
        transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
    }
    
    private class DoWithTenantCallback implements TransactionCallback {
        
        private Closure closure
        
        public DoWithTenantCallback(Closure closure) {
            this.closure = closure
        }
        
        @Override
        public Object doInTransaction(TransactionStatus status) {
            closure.call()
        }
    }

}