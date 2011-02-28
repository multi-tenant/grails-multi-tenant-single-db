package grails.plugin.multitenant.singledb.hibernate

import demo.DemoTenant;
import grails.plugin.spock.UnitSpec;
import grails.plugins.hawkeventing.BaseEvent;
import grails.plugins.hawkeventing.Event;
import grails.plugins.hawkeventing.EventBroker;
import grails.plugins.hawkeventing.EventConsumer

/**
 * 
 * @author Kim A. Betti
 */
class TenantHibernateEventProxySpec extends UnitSpec {

    def "subscriptions are added for relevant tenant events"() {
        given:
        TenantHibernateEventProxy proxy = new TenantHibernateEventProxy()
        proxy.eventBroker = Mock(EventBroker)
        
        when:
        proxy.registerTenantListeners(DemoTenant)
        
        then:
        1 * proxy.eventBroker.subscribe("hibernate.postInsert.demoTenant", _ as EventConsumer)
        1 * proxy.eventBroker.subscribe("hibernate.postDelete.demoTenant", _ as EventConsumer)
        1 * proxy.eventBroker.subscribe("hibernate.postUpdate.demoTenant", _ as EventConsumer)
    }
    
    def "events are being re-published with new event name"() {
        given:
        EventBroker mockedEventBroker = Mock()
        HawkEventProxy proxy = new HawkEventProxy(mockedEventBroker, "tenant.created")
        
        when:
        Event event = new BaseEvent("hibernate.postInsert.demoTenant", null)
        proxy.consume(event)
        
        then:
        1 * mockedEventBroker.publish("tenant.created", _)
    }
    
}
