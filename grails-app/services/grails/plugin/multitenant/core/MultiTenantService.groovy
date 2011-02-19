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
    
    void doWithTenantId(Integer tenantId, Closure closure) {
        Integer oldTenantId = currentTenant.get()
        try {
            currentTenant.set(tenantId)
            transactionTemplate.execute(new TransactionCallback() {
                public Object doInTransaction(TransactionStatus status) {
                    closure.call()
                }
            })
        } finally {
            currentTenant.set(oldTenantId)
        }
    }
    
    void setTransactionManager(PlatformTransactionManager transactionManager) {
        transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
    }

}