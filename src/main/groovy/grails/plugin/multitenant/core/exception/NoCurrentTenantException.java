package grails.plugin.multitenant.core.exception;

/**
 * Thrown when a current tenant isn't set.
 * @author Kim A. Betti
 */
@SuppressWarnings("serial")
public class NoCurrentTenantException extends TenantException {

    public NoCurrentTenantException(String message) {
        super(message);
    }

    public NoCurrentTenantException(String message, Throwable cause) {
        super(message, cause);
    }

}