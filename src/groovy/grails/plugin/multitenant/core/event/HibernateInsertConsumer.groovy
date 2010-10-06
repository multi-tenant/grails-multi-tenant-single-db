package grails.plugin.multitenant.core.event

import org.hibernate.event.PreInsertEvent
import org.hibernate.event.PreInsertEventListener
import org.hibernate.tuple.StandardProperty
import grails.plugin.multitenant.core.TenantUtils

class HibernateInsertConsumer implements PreInsertEventListener {

	def eventBroker
	def currentTenant
	
	@Override
	boolean onPreInsert(PreInsertEvent event) {
		def entity = event.getEntity()
		if (TenantUtils.hasMultiTenantAnnotation(entity.getClass())) {
			int currentTenantId = currentTenant.get()
			entity.tenantId = currentTenantId
			updateState(event, currentTenantId)
		}
		
		return false
	}

	private void updateState(PreInsertEvent event, int currentTenantId) {
		int paramIndex = getParamIndex(event)
		event.getState()[paramIndex] = currentTenantId
	}

	private int getParamIndex(PreInsertEvent event) {
		int i = 0;
		StandardProperty[] properties = event.getPersister().getEntityMetamodel().getProperties()
		for (StandardProperty p : properties) {
			if (p.name == 'tenantId')
				return i
			else i++
		}
		
		throw new Exception("Unable to find tenantId index for entity: " + event.getEntity());
	}
		
}
