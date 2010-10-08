package grails.plugin.multitenant.core.util

import grails.plugin.multitenant.core.hibernate.TenantFilterCfg;

/**
 * Helper methods for setting and getting tenantId on entities. 
 * 
 * This is useful when for use with Java 
 * (since javac doesn't recognize ast transformations). 
 * 
 * @author Kim A. Betti
 */
class MtDomainClassUtil {

    public static Integer getTenantIdFromEntity(def entity) {
        return entity.properties[TenantFilterCfg.TENANT_ID_FIELD_NAME];
    }
    
    public static void setTenantIdOnEntity(def entity, Integer tenantId) {
        entity.properties[TenantFilterCfg.TENANT_ID_FIELD_NAME] = tenantId;
    }
    
}
