package grails.plugin.multitenant.core.spring;

import grails.plugin.multitenant.core.CurrentTenant;
import grails.plugin.multitenant.core.CurrentTenantAware;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Inject the currentTenant bean into beans
 * implementing the CurrentTenantAware interface.
 * @author Kim A. Betti
 */
public class CurrentTenantAwarePostProcessor implements BeanPostProcessor {

    private CurrentTenant currentTenant;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof CurrentTenantAware) {
            CurrentTenantAware currentTenantAwareBean = (CurrentTenantAware) bean;
            currentTenantAwareBean.setCurrentTenant(currentTenant);
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public void setCurrentTenant(CurrentTenant currentTenant) {
        this.currentTenant = currentTenant;
    }

}