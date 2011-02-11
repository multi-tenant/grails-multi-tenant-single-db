package grails.plugin.multitenant.core.hibernate.event;

import grails.plugin.hibernatehijacker.hibernate.events.HibernateEventUtil;
import grails.plugin.multitenant.core.CurrentTenant;
import grails.plugin.multitenant.core.MultiTenantContext;
import grails.plugin.multitenant.core.exception.TenantException;
import grails.plugin.multitenant.core.exception.TenantSecurityException;
import grails.plugin.multitenant.core.util.MtDomainClassUtil;
import grails.plugin.multitenant.singledb.hibernate.TenantFilterCfg;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.EventListeners;
import org.hibernate.event.LoadEvent;
import org.hibernate.event.LoadEventListener;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;
import org.hibernate.tuple.StandardProperty;

/**
 * TODO: Think error handling all the way through. The original multi-tenant
 * plugin deals with error handling by returning 'false' from the callback
 * methods where this is possible. I think throwing an exception is more
 * informative, but there might be reasons why logging the error and returning
 * false is a better idea?
 * 
 * @author Kim A. Betti
 */
@SuppressWarnings("serial")
public class TenantHibernateEventListener implements PreInsertEventListener, PreUpdateEventListener, LoadEventListener {

    private static Log log = LogFactory.getLog(TenantHibernateEventListener.class);

    private CurrentTenant currentTenant;
    private MultiTenantContext multiTenantContext;

    // The PostLoad event only contains the class name so reflection is used to
    // load the corresponding class. This is obviously expensive so we cache the
    // result.
    private Map<String, Class<?>> reflectedCache = new HashMap<String, Class<?>>();

    public void activate(Configuration configuration) {
        log.debug("Subscribing to preInsert, preUpdate and postLoad");
        EventListeners eventListeners = configuration.getEventListeners();
        HibernateEventUtil.addListener(eventListeners, "load", this);
        HibernateEventUtil.addListener(eventListeners, "preInsert", this);
        HibernateEventUtil.addListener(eventListeners, "preUpdate", this);
    }

    public void setCurrentTenant(CurrentTenant currentTenant) {
        this.currentTenant = currentTenant;
    }

    public void setMultiTenantContext(MultiTenantContext multiTenantContext) {
        this.multiTenantContext = multiTenantContext;
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        if (isMultiTenantEntity(event.getEntity())) {
            Integer currentTenantId = currentTenant.get();
            if (currentTenantId <= 0) {
                throw new TenantException("Tried to save multi-tenant domain class '" + event.getEntity() + "', but no tenant is set");
            }

            MtDomainClassUtil.setTenantIdOnEntity(event.getEntity(), currentTenantId);
            updateTenantIdOnEvent(event, currentTenantId);
        }

        return false;
    }

    private void updateTenantIdOnEvent(PreInsertEvent event, Integer currentTenantId) {
        int paramIndex = getParamIndex(event);
        event.getState()[paramIndex] = currentTenantId;
    }

    // TODO: Is it worth caching this?
    private int getParamIndex(PreInsertEvent event) {
        int i = 0;
        StandardProperty[] properties = event.getPersister().getEntityMetamodel().getProperties();
        for (StandardProperty p : properties) {
            if (p.getName().equals(TenantFilterCfg.TENANT_ID_FIELD_NAME)) {
                return i;
            }

            i++;
        }

        throw new TenantException("Unable to find tenantId index for entity: " + event.getEntity());
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        if (isMultiTenantEntity(event.getEntity())) {
            Integer currentTenantId = currentTenant.get();
            Integer entityTenantId = MtDomainClassUtil.getTenantIdFromEntity(event.getEntity());
            if (!currentTenantId.equals(entityTenantId)) {
                throw new TenantSecurityException("Tried to update '" + event.getEntity() + "' with another tenant id. Expected " + currentTenantId + ", found " + entityTenantId, currentTenantId,
                        entityTenantId);
            }
        }

        return false;
    }

    @Override
    public void onLoad(LoadEvent event, LoadType type) throws HibernateException {
        Class<?> entityClass = getClassFromName(event.getEntityClassName());
        Object result = event.getResult();
        if (result != null && isMultiTenantEntity(result)) {
            Integer currentTenantId = currentTenant.get();
            Integer loadedTenantId = MtDomainClassUtil.getTenantIdFromEntity(result);
            if (!currentTenantId.equals(loadedTenantId) && !event.isAssociationFetch()) {
                log.warn("Tried to load entity '" + entityClass.getSimpleName() + "' from other tenant, expected " + currentTenantId + ", found " + loadedTenantId);

                event.setResult(null);
            }
        }
    }

    private Class<?> getClassFromName(String className) {
        if (!reflectedCache.containsKey(className)) {
            try {
                Class<?> aClass = getClass().getClassLoader().loadClass(className);
                reflectedCache.put(className, aClass);
            } catch (ClassNotFoundException ex) {
                String message = "Could not find class " + className;
                throw new TenantException(message, ex);
            }
        }

        return reflectedCache.get(className);
    }

    private boolean isMultiTenantEntity(Object entity) {
        Class<?> entityClass = entity.getClass();
        return multiTenantContext.isMultiTenantDomainClass(entityClass);
    }

}
