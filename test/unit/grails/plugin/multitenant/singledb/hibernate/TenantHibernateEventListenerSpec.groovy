package grails.plugin.multitenant.singledb.hibernate

import org.hibernate.event.PreInsertEvent;
import org.hibernate.persister.entity.EntityPersister;

import grails.plugin.multitenant.core.CurrentTenant;
import grails.plugin.multitenant.core.MultiTenantDomainClass;
import grails.plugin.multitenant.core.exception.NoCurrentTenantException;
import grails.plugin.multitenant.core.exception.TenantException;
import grails.plugin.spock.UnitSpec;


/**
 * 
 * @author Kim A. Betti
 */
class TenantHibernateEventListenerSpec extends UnitSpec {
    
    TenantHibernateEventListener eventListener
    
    def setup() {
        eventListener = new TenantHibernateEventListener()
    }

    def "pre insert without current tenant throws exception"() {
        given: "a pre insert event"
        DummyEntity entity = new DummyEntity()
        PreInsertEvent event = new PreInsertEvent(entity, null, null, null, null)
        
        and: "a mocked currentTenant bean"
        eventListener.currentTenant = Mock(CurrentTenant)
        
        when: "the event listener is invoked"
        eventListener.onPreInsert(event)
        
        then: "currentTenant is asked and returns null"
        eventListener.currentTenant.get() >> null
        
        and: "exception is thrown"
        NoCurrentTenantException ex = thrown()
    }

    private class DummyEntity implements MultiTenantDomainClass {
        Integer tenantId
    }
        
}
