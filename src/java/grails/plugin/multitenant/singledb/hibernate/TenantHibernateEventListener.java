package grails.plugin.multitenant.singledb.hibernate;

import grails.plugin.multitenant.core.CurrentTenant;
import grails.plugin.multitenant.core.MultiTenantDomainClass;
import grails.plugin.multitenant.core.ast.MultiTenantAST;
import grails.plugin.multitenant.core.exception.NoCurrentTenantException;
import grails.plugin.multitenant.core.exception.TenantException;
import grails.plugin.multitenant.core.exception.TenantSecurityException;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.event.LoadEvent;
import org.hibernate.event.LoadEventListener;
import org.hibernate.event.PostLoadEvent;
import org.hibernate.event.PostLoadEventListener;
import org.hibernate.event.PreDeleteEvent;
import org.hibernate.event.PreDeleteEventListener;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;
import org.hibernate.tuple.StandardProperty;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for pre-insert, pre-update and load / fetch Hibernate events. If the
 * domain class related to the event is a multi-tenant class we apply the
 * relevant constraints.
 * 
 * @author Kim A. Betti
 */
@SuppressWarnings("serial")
public class TenantHibernateEventListener implements PreInsertEventListener, PreUpdateEventListener, LoadEventListener, PostLoadEventListener, PreDeleteEventListener {

    private static Logger log = LoggerFactory.getLogger(TenantHibernateFilterConfigurator.class);

    private CurrentTenant currentTenant;

    // We need to get the index of the tenantId property.
    // This is another expensive operation so the result is cached here.
    private Map<Class<? extends MultiTenantDomainClass>, Integer> entityParamIndexCache = new HashMap<Class<? extends MultiTenantDomainClass>, Integer>();

    /**
     * One important thing to know. It's not enough to update the entity
     * instance with the new tenant-id. Hibernate will _not_ pick up on this and
     * will therefore not save the updated tenant id to database.
     * 
     * We have to get hold of the JPA meta model, find the index of the tenantId
     * field and update the entity state in the event.
     */
    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        if (isMultiTenantEntity(event.getEntity())) {
            Integer currentTenantId = currentTenant.get();
            if (currentTenantId == null) {
                throw new NoCurrentTenantException("Tried to save multi-tenant domain class '"
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

    @SuppressWarnings("unchecked")
    private int getTenantIdParamIndex(PreInsertEvent event) {
        Class<? extends MultiTenantDomainClass> entityClass = (Class<? extends MultiTenantDomainClass>) event.getEntity().getClass();
        if (!entityParamIndexCache.containsKey(entityClass)) {
            EntityMetamodel metaModel = event.getPersister().getEntityMetamodel();
            int propertyIndex = getPropertyIndex(metaModel, MultiTenantAST.TENANT_ID_FIELD_NAME);
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
    public void onLoad(LoadEvent event, LoadType loadType) throws HibernateException {
        Object LoadedEntity = event.getResult();
        if (LoadedEntity != null && isMultiTenantEntity(LoadedEntity)) {
            MultiTenantDomainClass entity = (MultiTenantDomainClass) LoadedEntity;

            // We won't be able to extract tenant-id from an association fetch.
            // TODO: This is a bit scary as it means that we potentially can load entities from
            // other tenants through various variants of Hibernate collection / lazy loading.
            if (!event.isAssociationFetch() && !allowEntityLoad(entity)) {
                log.debug("Refusing tenant {} to load {}", currentTenant.get(), entity.getClass().getSimpleName());
                event.setResult(null);
            }
        }
    }

    private boolean allowEntityLoad(MultiTenantDomainClass entity) {
        if (currentTenant.isSet()) {
            if (belongsToCurrentTenant(entity)) {
                return true;
            } else {
                log.warn("Tried to fetch an instance of {} belonging to {}", entity.getClass().getName(), entity.getTenantId());
                return false;
            }
        } else {
            return true; // No current tenant => no restrictions
        }
    }



    /**
     * This is our last chance to detect attempts to load entities belonging to
     * other tenants.
     */
    @Override
    public void onPostLoad(PostLoadEvent event) {
        // This works, but has the down side that it also triggers an
        // exception for situations we've already handled more gracefully
        // pretending that the entity wasn't found.

        //        Object entity = event.getEntity();
        //        if (isMultiTenantEntity(entity)) {
        //            System.out.println("Verifying post load of " + event.getEntity());
        //            if (currentTenant.isSet()) {
        //                MultiTenantDomainClass tenantEntity = (MultiTenantDomainClass) entity;
        //                System.out.println(" -> post load tenant id: " + tenantEntity.getTenantId());
        //
        //                Integer tenantEntityId = tenantEntity.getTenantId();
        //                if (!currentTenant.get().equals(tenantEntityId)) {
        //                    System.out.println(" -> Warning! Detected another tenant");
        //                    throw new TenantSecurityException("Tried to load '" + tenantEntity + "' with another tenant id. Expected "
        //                            + currentTenant.get() + ", found " + tenantEntityId, currentTenant.get(), tenantEntityId);
        //                }
        //            }
        //        }
    }


    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        boolean shouldVetoDelete = false;
        if (isMultiTenantEntity(event.getEntity())) {
            MultiTenantDomainClass tenantEntity = (MultiTenantDomainClass) event.getEntity();
            if (!belongsToCurrentTenant(tenantEntity)) {
                log.warn("Tenant {} tried to delete another tenants entity {}", currentTenant.get(), tenantEntity);
                shouldVetoDelete = true;
            }
        }

        return shouldVetoDelete;
    }

    private boolean belongsToCurrentTenant(MultiTenantDomainClass entity) {
        return currentTenant.isSet() && currentTenant.get().equals(entity.getTenantId());
    }

    private boolean isMultiTenantEntity(Object entity) {
        return entity instanceof MultiTenantDomainClass;
    }

    public void setCurrentTenant(CurrentTenant currentTenant) {
        this.currentTenant = currentTenant;
    }

}