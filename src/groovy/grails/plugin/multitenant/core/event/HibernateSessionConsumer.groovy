package grails.plugin.multitenant.core.event


import org.hibernate.classic.Session
import org.springframework.beans.factory.InitializingBean

import grails.plugin.multitenant.core.TenantUtils
import grails.plugin.eventing.EventBroker
import grails.plugin.eventing.EventConsumer

class HibernateSessionConsumer implements InitializingBean, EventConsumer {

	final String name = "MultiTenantHibernateFilterActivator"
	final String eventName = "hibernate.sessionCreated"
	
	def currentTenant
	def eventBroker
		
	void afterPropertiesSet() {
		eventBroker.subscribe(eventName, this)
	}
	
	void consume(Object arg, EventBroker broker) {
		int currentTenantId = currentTenant.get();
		if (currentTenantId >= 0)
			TenantUtils.enableHibernateFilter((Session) arg, currentTenantId);
	}
	
}