package grails.plugin.multitenant.core.servlet

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import grails.plugin.multitenant.core.CurrentTenant;
import grails.plugin.multitenant.core.resolve.TenantResolver;
import grails.plugin.spock.UnitSpec;

/**
 * 
 * @author Kim A. Betti
 */
class CurrentTenantServletFilterSpec extends UnitSpec {

    def "current tenant should be sat to null after an exception in the filter chain"() {
        given: "a filter instance and mocked tenant classes"
        CurrentTenantServletFilter filter = new CurrentTenantServletFilter()
        filter.tenantResolver = Mock(TenantResolver)
        filter.currentTenant = Mock(CurrentTenant)
                
        and: "mocked request, response and filter chain instances"
        ServletRequest mockedRequest = new MockHttpServletRequest()
        ServletResponse mockedResponse = new MockHttpServletResponse()
        FilterChain mockedFilterChain = new ExceptionThrowingFilterChain()
        
        when: "we invoke the filter with an exception throwing filter chain"
        filter.doFilter(mockedRequest, mockedResponse, mockedFilterChain)
        
        then: "the tenant is resolved as expected"
        1 * filter.tenantResolver.resolve(_ as HttpServletRequest) >> 123
        
        and: "the current tenant is updated with the resolved tenant id"
        1 * filter.currentTenant.set(123)
        
        and: "the exception is thrown from the filter"
        RuntimeException rex = thrown()
        rex.message == "Something went wrong in the filter chain"
        
        and: "current tenant is set to null"
        1 * filter.currentTenant.set(null)
    }

    private class ExceptionThrowingFilterChain implements FilterChain {

        @Override
        public void doFilter(ServletRequest req, ServletResponse res) throws IOException, ServletException {
            throw new RuntimeException("Something went wrong in the filter chain")
        }
        
    }
    
}
