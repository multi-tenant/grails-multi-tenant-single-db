package grails.plugin.multitenant.core.filter;

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

public class CurrentTenantFilter implements Filter {

    private CurrentTenant currentTenant;
    private TenantResolver tenantResolver;

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

        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            int currentTenantId = tenantResolver.resolve(httpRequest);
            currentTenant.set(currentTenantId);
        }

        filterChain.doFilter(request, response);
        currentTenant.set(-1);
    }

    @Override
    public void destroy() {
    }	
	
}
