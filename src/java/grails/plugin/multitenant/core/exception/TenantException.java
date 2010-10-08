package grails.plugin.multitenant.core.exception;

@SuppressWarnings("serial")
public class TenantException extends RuntimeException {

    public TenantException(String message) {
        super(message);
    }
    
}
