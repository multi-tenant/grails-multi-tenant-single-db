package grails.plugin.multitenant.core.exception;

@SuppressWarnings("serial")
public class TenantSecurityException extends TenantException {

    private Long currentTenantId, loadedTenantId;

    public TenantSecurityException(String message, Long currentTenantId, Long loadedTenantId) {
        super(message);
        this.currentTenantId = currentTenantId;
        this.loadedTenantId = loadedTenantId;
    }

    public Long getCurrentTenantId() {
        return currentTenantId;
    }

    public Long getLoadedTenantId() {
        return loadedTenantId;
    }

}
