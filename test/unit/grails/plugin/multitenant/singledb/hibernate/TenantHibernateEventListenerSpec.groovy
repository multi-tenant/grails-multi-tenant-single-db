package grails.plugin.multitenant.singledb.hibernate

import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.persister.entity.EntityPersister;

import spock.lang.Unroll;

import grails.plugin.hibernatehijacker.hibernate.events.HibernateEventPropertyUpdater
import grails.plugin.multitenant.core.CurrentTenant;
import grails.plugin.multitenant.core.MultiTenantDomainClass;
import grails.plugin.multitenant.core.Tenant;
import grails.plugin.multitenant.core.exception.NoCurrentTenantException;
import grails.plugin.multitenant.core.exception.TenantException;
import grails.plugin.multitenant.core.exception.TenantSecurityException;
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
        eventListener.currentTenant.isSet() >> false
        
        and: "exception is thrown"
        NoCurrentTenantException ex = thrown()
    }
    
    def "tenant id is sat on the entity and event state on preInsert"() {
        given:
        DummyEntity entity = new DummyEntity()
        PreInsertEvent event = new PreInsertEvent(entity, null, null, null, null)
        
        and: "current tenant returning 123 as tenant id" 
        eventListener.currentTenant = Mock(CurrentTenant)
        1 * eventListener.currentTenant.isSet() >> true
        1 * eventListener.currentTenant.get() >> 123
        
        and: "mocked event state updater"
        HibernateEventPropertyUpdater propertyUpdater = Mock()
        eventListener.hibernateEventPropertyUpdater = propertyUpdater
        
        when: "we invoke the event method"
        boolean vetoInsert = eventListener.onPreInsert(event)
        
        then: "event state is updated"
        1 * propertyUpdater.updateProperty(event, "tenantId", 123)
        
        and: "the actual entity instance is updated"
        entity.tenantId == 123
        
        and: "we dont veto the event"
        vetoInsert == false
    }
    
    @Unroll({"Allow #currentTenantId to allow an entity owned by #entityTenantId, #message"})
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
    
    @Unroll({"Tenant id #tenantId, current tenant #currentTenantId, belongs to current tenant #belongsToCurrent"})
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
       
    def "update without tenant id is allowed"() {
        given: "a mocked currentTenant bean"
        eventListener.currentTenant = Mock(CurrentTenant)
        eventListener.currentTenant.get() >> null
        
        expect: "the listener should not veto the event"
        def entity = new DummyEntity(tenantId: 123)
        def preUpdateEvent = new PreUpdateEvent(entity, null, null, null, null, null)
        eventListener.onPreUpdate(preUpdateEvent) == false
    }
    
    def "attempts to update another tenants entity should throw an exception"() {
        given: "a mocked currentTenant bean"
        eventListener.currentTenant = Mock(CurrentTenant)
        eventListener.currentTenant.get() >> 123
        
        when: "we try to update anther tenants entity"
        def entity = new DummyEntity(tenantId: 456)
        def preUpdateEvent = new PreUpdateEvent(entity, null, null, null, null, null)
        eventListener.onPreUpdate(preUpdateEvent)
        
        then: "a tenant security exception is thrown"
        thrown(TenantSecurityException)
    }
    
}

class DummyEntity implements MultiTenantDomainClass {
    Integer tenantId
}
