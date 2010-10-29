package grails.plugin.multitenant.core;

public interface CurrentTenant {
	
	Integer get();
	
    void set(Integer tenantId);
    
}