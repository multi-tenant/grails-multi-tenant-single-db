package grails.plugin.multitenant.core;

public interface CurrentTenant {
	int get();
    void set(int tenantId);
}
