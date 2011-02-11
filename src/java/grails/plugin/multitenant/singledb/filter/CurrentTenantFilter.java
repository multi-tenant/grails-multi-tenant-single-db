package grails.plugin.multitenant.singledb.filter;

import grails.plugin.multitenant.singledb.CurrentTenant;
import grails.plugin.multitenant.singledb.resolve.TenantResolver;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 
 * @author Kim A. Betti
 */
public class CurrentTenantFilter implements Filter {

    private CurrentTenant currentTenant; // thread local storage
    private TenantResolver tenantResolver; // provided by implementation 

    @Override
    public void init(FilterConfig fc) throws ServletException {
        ServletContext servletContext = fc.getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        currentTenant = (CurrentTenant) ctx.getBean("currentTenant");
        tenantResolver = (TenantResolver) ctx.getBean("tenantResolver");
    }
   
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
    		FilterChain filterChain) throws IOException, ServletException {
    	
    	try {
	        if (request instanceof HttpServletRequest) {
	            HttpServletRequest httpRequest = (HttpServletRequest) request;
	            int currentTenantId = tenantResolver.resolve(httpRequest);
	            currentTenant.set(currentTenantId);
	        }
	
	        filterChain.doFilter(request, response);
    	} finally {
    		currentTenant.set(-1);
    	}
    }

    @Override
    public void destroy() {
    }
	
}