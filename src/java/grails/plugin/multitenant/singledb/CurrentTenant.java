package grails.plugin.multitenant.singledb;

public interface CurrentTenant {
	
	Integer get();
	
    void set(Integer tenantId);
    
}