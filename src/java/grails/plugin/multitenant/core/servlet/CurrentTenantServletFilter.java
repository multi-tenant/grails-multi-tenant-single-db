package grails.plugin.multitenant.core.servlet;

import grails.plugin.multitenant.core.CurrentTenant;
import grails.plugin.multitenant.core.resolve.TenantResolver;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * This filter will be applied to incoming http requests.
 * The current tenant id is looked up using the application
 * provided tenantResolver and saved to a thread local storage.
 * 
 * The thread local storage is nulled out when other filters
 * and servlet has processed the request.
 * @author Kim A. Betti
 */
public class CurrentTenantServletFilter implements Filter {

    protected CurrentTenant currentTenant; // thread local storage
    protected TenantResolver tenantResolver; // provided by implementation

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
                Integer currentTenantId = tenantResolver.resolve(httpRequest);
                currentTenant.set(currentTenantId);
            }

            filterChain.doFilter(request, response);
        } finally {
            currentTenant.set(null);
        }
    }

    @Override
    public void destroy() {
    }

}