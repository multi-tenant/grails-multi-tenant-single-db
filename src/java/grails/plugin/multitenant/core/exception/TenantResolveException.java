package grails.plugin.multitenant.core.exception;

/**
 * Thrown when a tenant can't be resolved. 
 * @author Kim A. Betti
 */
@SuppressWarnings("serial")
public class TenantResolveException extends TenantException {

    public TenantResolveException(String message) {
        super(message);
    }

    public TenantResolveException(String message, Throwable cause) {
        super(message, cause);
    }

}