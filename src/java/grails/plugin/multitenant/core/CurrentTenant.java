package grails.plugin.multitenant.core;

public interface CurrentTenant {

    static final String TENANT_CHANGE_EVENT = "multitenant.currentTenantId";

    Integer get();

    void set(Integer tenantId);

}