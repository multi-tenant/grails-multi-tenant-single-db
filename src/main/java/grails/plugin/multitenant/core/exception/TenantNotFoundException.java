package grails.plugin.multitenant.core.exception;

/**
 * Thrown when a tenant can't be found.
 * @author Kim A. Betti
 */
@SuppressWarnings("serial")
public class TenantNotFoundException extends TenantException {

    private Integer missingTenantId;

    public TenantNotFoundException(Integer tenantId) {
        super("Unable to find tenant with id " + tenantId);
        this.missingTenantId = tenantId;
    }

    public TenantNotFoundException(String message) {
        super(message);
    }

    public TenantNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public Integer getMissingTenantId() {
        return missingTenantId;
    }

}