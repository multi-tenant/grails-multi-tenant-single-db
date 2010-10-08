package grails.plugin.multitenant.core.resolve;

import javax.servlet.http.HttpServletRequest;

/**
 * Resolves tenant id based on http request. 
 * 
 * TODO: Make a more pluggable / configurable approach
 *       to how the current tenant is looked up
 */
public interface TenantResolver {

	int resolve(HttpServletRequest request);
	
}
