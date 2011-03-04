package grails.plugin.multitenant.singledb.hibernate

import org.hibernate.event.PreInsertEvent;
import org.hibernate.persister.entity.EntityPersister;

import spock.lang.Unroll;

import grails.plugin.multitenant.core.CurrentTenant;
import grails.plugin.multitenant.core.MultiTenantDomainClass;
import grails.plugin.multitenant.core.Tenant;
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
    
    @Unroll("Allow #currentTenantId to allow an entity owned by #entityTenantId, #message")
    def "allowEntityLoad tests"() {
        expect:
        def entity = new DummyEntity(tenantId: entityTenantId)
        eventListener.allowEntityLoad(currentTenantId, entity) == shouldAllow
        
        where:
        currentTenantId   | entityTenantId  | shouldAllow   | message
        null              | 123             | true          | "No tenant, no restriction"
        123               | 123             | true          | "Must be able to load its own entities"
        123               | 321             | false         | "Should not be allowed to load others"
        123               | null            | false         | "We dont allow loading of entities without tenant id" // Can be discussed..
    }
    
    @Unroll("Tenant id #tenantId, current tenant #currentTenantId, belongs to current tenant #belongsToCurrent")
    def "belongsToCurrentTenant tests"() {
        expect:
        def entity = new DummyEntity(tenantId: entityTenantId)
        eventListener.belongsToCurrentTenant(currentTenantId, entity) == belongsToCurrent
        
        where:
        currentTenantId   | entityTenantId  | belongsToCurrent
        123               | 123             | true
        123               | null            | false
        null              | 123             | false
        null              | null            | false
    }
       
}

class DummyEntity implements MultiTenantDomainClass {
    Integer tenantId
}
