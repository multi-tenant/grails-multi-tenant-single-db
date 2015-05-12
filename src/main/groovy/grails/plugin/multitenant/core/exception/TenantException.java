package grails.plugin.multitenant.core.exception;

/**
 * Superclass for all multi tenant related exceptions.
 * @author Kim A. Betti
 */
@SuppressWarnings("serial")
public class TenantException extends RuntimeException {

    public TenantException(String message) {
        super(message);
    }

    public TenantException(String message, Throwable cause) {
        super(message, cause);
    }

}