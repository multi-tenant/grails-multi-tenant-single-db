package grails.plugin.multitenant.singledb.spring;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import grails.plugin.multitenant.singledb.CurrentTenant;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.ApplicationContext;

/**
 * 
 * @author Kim A. Betti
 */
public class TenantScope implements Scope {
	
	public static final String NAME = "tenant";
	
	private CurrentTenant currentTenant;
	private ApplicationContext applicationContext;
	
	private ConcurrentHashMap<Integer, Map<String, Object>> tenantBeanCache
		= new ConcurrentHashMap<Integer, Map<String, Object>>(100);

	
	/**
	 * Return the object with the given name from the underlying scope, 
	 * creating it if not found in the underlying storage mechanism.
	 */
	@Override
	public Object get(String name, ObjectFactory<?> objectFactory) {
		Integer tenantId = currentTenant.get();
		Map<String, Object> beanCache = getBeanCacheForTenant(tenantId);
		if (!beanCache.containsKey(name)) 
			beanCache.put(name, applicationContext.getBean(name + "_prototype"));
		
		return beanCache.get(name);
	}
	
	private Map<String, Object> getBeanCacheForTenant(Integer tenantId) {
		if (!tenantBeanCache.containsKey(tenantId))
			tenantBeanCache.put(tenantId, new ConcurrentHashMap<String, Object>(100));
		
		return tenantBeanCache.get(tenantId);
	}

	/**
	 * Return the conversation ID for the current underlying scope, if any.
	 */
	@Override
	public String getConversationId() {
		throw new RuntimeException("getConversationId is not implemented");
	}

	/**
	 * Register a callback to be executed on destruction of the specified object in the scope (or at destruction of the entire scope, 
	 * if the scope does not destroy individual objects but rather only terminates in its entirety).
	 */
	@Override
	public void registerDestructionCallback(String name, Runnable callback) {
		throw new RuntimeException("registerDestructionCallback " + name + " is not implemented");
	}

	/**
	 * Remove the object with the given name from the underlying scope.
	 */
	@Override
	public Object remove(String name) {
		throw new RuntimeException("remove " + name + " is not implemented");
	}

	/**
	 * Resolve the contextual object for the given key, if any.
	 */
	@Override
	public Object resolveContextualObject(String key) {
		throw new RuntimeException("resolveContextualObject " + key + " is not implemented");
	}

	public void setCurrentTenant(CurrentTenant currentTenant) {
		this.currentTenant = currentTenant;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
