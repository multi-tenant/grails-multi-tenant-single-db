package grails.plugin.multitenant.singledb.hibernate;

/**
 * Keeping these in a Groovy class caused problems when referenced from
 * Java so I moved them over here. 
 * 
 * @author Kim A. Betti
 */
public class TenantFilterCfg {
    
    public static final String TENANT_FILTER_NAME = "onlyCurrentTenant";
    public static final String TENANT_ID_PARAM_NAME = "tenantIdParam";
    public static final String TENANT_ID_FIELD_NAME = "tenantId";
    public static final String FILTER_CONDITION = ":tenantIdParam = tenant_id";
    
}