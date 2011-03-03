package grails.plugin.multitenant.core

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * Allows for temporary manipulation of the current tenant.
 * Note: If you use this too often you're probably doing something wrong.
 * @author Kim A. Betti
 */
class MultiTenantService {

    static transactional = false

    CurrentTenant currentTenant
    TransactionTemplate transactionTemplate
    SessionFactory sessionFactory
    
    /**
     * Execute some code in the 'namespace' of the given tenant id. 
     * The code will be executed in a new transaction with a new session 
     * to avoid other tenants entities laying in the first level cache to leak in. 
     */
    def doWithTenantId(Integer tenantId, Closure callback) {
        withNewSession { Session hibernateSession ->
            withTransaction { TransactionStatus status ->
                withTenantIdInTransaction(tenantId, callback, status)
            }    
        }
    }
    
    def withTenantIdInTransaction(Integer tenantId, Closure callback, TransactionStatus status) {
        Integer oldTenantId = currentTenant.get()
        try {
            currentTenant.set(tenantId)
            callback.call()
        } catch (Throwable throwable) {
            status.setRollbackOnly()
            throw throwable
        } finally {
            currentTenant.set(oldTenantId)
        }
    }
    
    /**
     * Run a closure in a new session. Taken from HibernatePluginSupport 
     * so we can use it without having to know about a domain class at compile time.
     */
    def withNewSession(Closure callback) {
        HibernateTemplate template = new HibernateTemplate(sessionFactory)
        SessionHolder sessionHolder = TransactionSynchronizationManager.getResource(sessionFactory)
        Session previousSession = sessionHolder?.session
        try {
            template.alwaysUseNewSession = true
            template.execute({ Session session ->
                if(sessionHolder == null) {
                    sessionHolder = new SessionHolder(session)
                    TransactionSynchronizationManager.bindResource(sessionFactory, sessionHolder)
                } else {
                    sessionHolder.addSession(session)
                }

                callback(session)
            } as HibernateCallback)
        } finally {
            if (previousSession) {
                sessionHolder?.addSession(previousSession)
            }
        }
    }
    
    /**
     * Injected by Spring
     * @param transactionManager
     */
    void setTransactionManager(PlatformTransactionManager transactionManager) {
        transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
    }
    
    def withTransaction(Closure callback) {
        transactionTemplate.execute({ status ->
            callback.call(status)
        } as TransactionCallback)
    }
    
}