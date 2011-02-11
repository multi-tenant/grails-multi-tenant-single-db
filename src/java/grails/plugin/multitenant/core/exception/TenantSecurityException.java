package grails.plugin.multitenant.core.exception;

@SuppressWarnings("serial")
public class TenantSecurityException extends TenantException {

    private Integer currentTenantId, loadedTenantId;

    public TenantSecurityException(String message, Integer currentTenantId, Integer loadedTenantId) {
        super(message);
        this.currentTenantId = currentTenantId;
        this.loadedTenantId = loadedTenantId;
    }

    public Integer getCurrentTenantId() {
        return currentTenantId;
    }

    public Integer getLoadedTenantId() {
        return loadedTenantId;
    }

}
