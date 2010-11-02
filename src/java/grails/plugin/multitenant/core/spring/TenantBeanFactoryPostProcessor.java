package grails.plugin.multitenant.core.spring;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.codehaus.groovy.grails.commons.spring.*;

import org.springframework.core.Ordered;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.*;
import org.springframework.aop.scope.ScopedProxyFactoryBean;

/**
 * 
 * @author Kim A. Betti
 */
public class TenantBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {

	private static final Log log = LogFactory.getLog(TenantBeanFactoryPostProcessor.class);
	private List<String> perTenantBeans;
	
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		BeanDefinitionRegistry appCtx = (DefaultListableBeanFactory) beanFactory;
		RuntimeSpringConfiguration springConfig = new DefaultRuntimeSpringConfiguration(); 
		
		for (String beanName : getPerTenantBeanNames(appCtx)) {
			log.debug("Setting tenant scope for: " + beanName);
			
			BeanDefinition beanDef = appCtx.getBeanDefinition(beanName);
			processBean(beanName, beanDef, appCtx, springConfig);
		}
	}
	
	private void processBean(String beanName, BeanDefinition beanDef, BeanDefinitionRegistry appCtx, RuntimeSpringConfiguration springConfig) {
		final String tenantScopedBeanName = "_" + beanName;
		final String prototypeBeanName = tenantScopedBeanName + "_prototype";
		
		makeTenantScopedCopy(appCtx, beanDef, tenantScopedBeanName);
		makePrototypeScopedCopy(appCtx, beanDef, prototypeBeanName);
		replaceWithScopedProxy(appCtx, springConfig, beanName, tenantScopedBeanName);
	}
	
	private List<String> getPerTenantBeanNames(BeanDefinitionRegistry beanFactory) {
		List<String> perTenantBeanNames = new ArrayList<String>();
		for (String beanName : beanFactory.getBeanDefinitionNames()) {
			if (perTenantBeans.contains(beanName))
				perTenantBeanNames.add(beanName);
		}
		
		return perTenantBeanNames;
	}
	
	private BeanDefinition makeTenantScopedCopy(BeanDefinitionRegistry appCtx, BeanDefinition beanDef, String tenantScopedBeanName) {
		BeanDefinition tenantScopedBeanDef = copyBeanDefiniton(appCtx, beanDef, tenantScopedBeanName);
		tenantScopedBeanDef.setScope(TenantScope.NAME);
		return tenantScopedBeanDef;
	}
	
	private BeanDefinition makePrototypeScopedCopy(BeanDefinitionRegistry appCtx, BeanDefinition orgBeanDef, String prototypeBeanName) {
		BeanDefinition prototypeDefinition = copyBeanDefiniton(appCtx, orgBeanDef, prototypeBeanName);
		prototypeDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
		return prototypeDefinition;
	}
	
	private BeanDefinition copyBeanDefiniton(BeanDefinitionRegistry appCtx, BeanDefinition orgBeanDef, String newBeanName) {
		BeanDefinition copiedBeanDefinition = new GenericBeanDefinition(orgBeanDef);
        appCtx.registerBeanDefinition(newBeanName, copiedBeanDefinition);
        return copiedBeanDefinition;
	}
	
	private void replaceWithScopedProxy(BeanDefinitionRegistry appCtx, RuntimeSpringConfiguration springConfig, String beanName, String targetBeanName) {
		BeanConfiguration scopedProxy = springConfig.createSingletonBean(ScopedProxyFactoryBean.class);
		scopedProxy.addProperty("targetBeanName", targetBeanName);
		
		BeanDefinition scopedBeanDefinition = scopedProxy.getBeanDefinition();
		appCtx.registerBeanDefinition(beanName, scopedBeanDefinition);
	}
	
	public void setPerTenantBeans(List<String> perTenantBeans) {
		this.perTenantBeans = perTenantBeans;
	}
		
	@Override
	public int getOrder() {
		return 2;
	}

}
