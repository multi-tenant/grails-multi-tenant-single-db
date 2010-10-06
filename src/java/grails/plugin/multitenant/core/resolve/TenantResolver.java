package grails.plugin.multitenant.core.resolve;

import javax.servlet.http.HttpServletRequest;

public interface TenantResolver {

	int resolve(HttpServletRequest request);
	
}
