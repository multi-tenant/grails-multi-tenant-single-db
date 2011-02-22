package grails.plugin.multitenant.core.hibernate.event;

import grails.plugin.multitenant.core.CurrentTenant;
import grails.plugin.multitenant.core.MultiTenantDomainClass;
import grails.plugin.multitenant.core.exception.TenantException;
import grails.plugin.multitenant.core.exception.TenantSecurityException;
import grails.plugin.multitenant.singledb.hibernate.TenantFilterCfg;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.event.LoadEvent;
import org.hibernate.event.LoadEventListener;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;
import org.hibernate.tuple.StandardProperty;
import org.hibernate.tuple.entity.EntityMetamodel;

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

    // The PostLoad event only contains the class name so reflection is used to
    // load the corresponding class. This is obviously expensive so we cache the result.
    private Map<String, Class<?>> reflectedCache = new HashMap<String, Class<?>>();

    // We need to get the index of the tenantId property.
    // This is another expensive operation so the result is cached here.
    private Map<Class<?>, Integer> entityParamIndexCache = new HashMap<Class<?>, Integer>();

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        if (isMultiTenantEntity(event.getEntity())) {
            Integer currentTenantId = currentTenant.get();
            if (currentTenantId == null) {
                throw new TenantException("Tried to save multi-tenant domain class '"
                        + event.getEntity().getClass().getSimpleName() + "', but no tenant is set");
            }

            MultiTenantDomainClass entity = (MultiTenantDomainClass) event.getEntity();
            entity.setTenantId(currentTenantId);
            updateTenantIdOnEvent(event, currentTenantId);
        }

        return false;
    }

    private void updateTenantIdOnEvent(PreInsertEvent event, Integer currentTenantId) {
        int paramIndex = getTenantIdParamIndex(event);
        event.getState()[paramIndex] = currentTenantId;
    }

    private int getTenantIdParamIndex(PreInsertEvent event) {
        Class<?> entityClass = event.getEntity().getClass();
        if (!entityParamIndexCache.containsKey(entityClass)) {
            EntityMetamodel metaModel = event.getPersister().getEntityMetamodel();
            int propertyIndex = getPropertyIndex(metaModel, TenantFilterCfg.TENANT_ID_FIELD_NAME);
            entityParamIndexCache.put(entityClass, propertyIndex);
        }

        return entityParamIndexCache.get(entityClass);
    }

    private int getPropertyIndex(EntityMetamodel metaModel, String propertyName) {
        int i = 0;
        StandardProperty[] properties = metaModel.getProperties();
        for (StandardProperty property : properties) {
            if (property.getName().equals(propertyName)) {
                return i;
            }

            i++;
        }

        throw new TenantException("Unable to find property index for: " + propertyName);
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        if (isMultiTenantEntity(event.getEntity())) {
            Integer currentTenantId = currentTenant.get();
            MultiTenantDomainClass entity = (MultiTenantDomainClass) event.getEntity();
            Integer entityTenantId = entity.getTenantId();
            if (!currentTenantId.equals(entityTenantId)) {
                throw new TenantSecurityException("Tried to update '" + event.getEntity() + "' with another tenant id. Expected "
                        + currentTenantId + ", found " + entityTenantId, currentTenantId, entityTenantId);
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
            Integer loadedTenantId = ((MultiTenantDomainClass) result).getTenantId();
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
        return entity instanceof MultiTenantDomainClass;
    }

    public void setCurrentTenant(CurrentTenant currentTenant) {
        this.currentTenant = currentTenant;
    }

}