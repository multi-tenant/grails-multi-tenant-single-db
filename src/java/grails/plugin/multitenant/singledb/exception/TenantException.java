package grails.plugin.multitenant.singledb.exception;

@SuppressWarnings("serial")
public class TenantException extends RuntimeException {

    public TenantException(String message) {
        super(message);
    }

	public TenantException(String message, Throwable cause) {
		super(message, cause);
	}
    
}
