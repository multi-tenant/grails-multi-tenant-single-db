package grails.plugin.multitenant.core.spring;

import grails.plugin.multitenant.core.CurrentTenant;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;

/**
 * 
 * @author Kim A. Betti
 */
public class TenantScopeConfigurator implements BeanFactoryPostProcessor, Ordered, ApplicationContextAware {

    private static final Log log = LogFactory.getLog(TenantScopeConfigurator.class);

    private CurrentTenant currentTenant;
    private ApplicationContext applicationContext;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        log.debug("Creating tenant scope");

        TenantScope tenantScope = new TenantScope();
        tenantScope.setCurrentTenant(currentTenant);
        tenantScope.setApplicationContext(applicationContext);
        beanFactory.registerScope("tenant", tenantScope);
    }

    public void setCurrentTenant(CurrentTenant currentTenant) {
        this.currentTenant = currentTenant;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.applicationContext = ctx;
    }

}