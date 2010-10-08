package grails.plugin.multitenant.core.hibernate.event;

import grails.plugin.hibernatehijacker.hibernate.events.HibernateEventUtil;
import grails.plugin.multitenant.core.CurrentTenant;
import grails.plugin.multitenant.core.exception.TenantException;
import grails.plugin.multitenant.core.exception.TenantSecurityException;
import grails.plugin.multitenant.core.hibernate.TenantFilterCfg;
import grails.plugin.multitenant.core.util.MtDomainClassUtil;
import grails.plugin.multitenant.core.util.TenantUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.tuple.StandardProperty;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.*;

@SuppressWarnings("serial")
public class TenantHibernateEventListener implements PreInsertEventListener, PreUpdateEventListener, PostLoadEventListener {

    private static Log log = LogFactory.getLog(TenantHibernateEventListener.class);
    private CurrentTenant currentTenant;
    
    public void activate(Configuration configuration) {
        log.debug("Subscribing to preInsert, preUpdate and postLoad");
        EventListeners eventListeners = configuration.getEventListeners();
        HibernateEventUtil.addListener(eventListeners, "postLoad", this);
        HibernateEventUtil.addListener(eventListeners, "preInsert", this);
        HibernateEventUtil.addListener(eventListeners, "preUpdate", this);
    }
    
    public void setCurrentTenant(CurrentTenant currentTenant) {
        this.currentTenant = currentTenant;
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        if (isMultiTenantEntity(event.getEntity())) {
            Integer currentTenantId = currentTenant.get();
            if (currentTenantId <= 0) 
                throw new TenantException("Tried to save multi-tenant domain class '"
                        + event.getEntity() + "', but no tenant is set");
                
            MtDomainClassUtil.setTenantIdOnEntity(event.getEntity(), currentTenantId);
            updateState(event, currentTenantId);
        }
        
        return false;
    }
    
    private void updateState(PreInsertEvent event, Integer currentTenantId) {
        int paramIndex = getParamIndex(event);
        event.getState()[paramIndex] = currentTenantId;
    }

    private int getParamIndex(PreInsertEvent event) {
        int i = 0;
        StandardProperty[] properties = event.getPersister().getEntityMetamodel().getProperties();
        for (StandardProperty p : properties) {
            if (p.getName().equals(TenantFilterCfg.TENANT_ID_FIELD_NAME))
                return i;
            
            i++;
        }
        
        throw new TenantException("Unable to find tenantId index for entity: " + event.getEntity());
    }
    
    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        if (isMultiTenantEntity(event.getEntity())) {
            Integer currentTenantId = currentTenant.get();
            Integer entityTenantId = MtDomainClassUtil.getTenantIdFromEntity(event.getEntity());
            if (!currentTenantId.equals(entityTenantId)) 
                throw new TenantSecurityException("Tried to update '"+event.getEntity()+"' with another tenant id. Expected " 
                        + currentTenantId + ", found " + entityTenantId, currentTenantId, entityTenantId);
        }
        
        return false;
    }

    @Override
    public void onPostLoad(PostLoadEvent event) {
        if (isMultiTenantEntity(event.getEntity())) {
            Integer currentTenantId = currentTenant.get();
            Integer loadedTenantId = MtDomainClassUtil.getTenantIdFromEntity(event.getEntity());

            if (!currentTenantId.equals(loadedTenantId)) 
                throw new TenantSecurityException("Tried to load entity '" + event.getEntity() 
                        + "' from other tenant, expected " + currentTenantId + ", found " + loadedTenantId, currentTenantId, loadedTenantId);
        }
    }
    
    private boolean isMultiTenantEntity(Object entity) {
        return TenantUtils.hasMultiTenantAnnotation(entity.getClass());
    }

}
