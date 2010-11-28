import grails.util.Environment

import grails.plugin.multitenant.core.*
import grails.plugin.multitenant.core.util.*
import grails.plugin.multitenant.core.filter.CurrentTenantFilter
import grails.plugin.multitenant.core.hibernate.event.*
import grails.plugin.multitenant.core.hibernate.*
import grails.plugin.multitenant.core.spring.*

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class MultiTenantCoreGrailsPlugin {

    def version = "0.2.5"
    def grailsVersion = "1.3.5 > *"
     
    def dependsOn = [:]
    
    def loadAfter = [ 'hawk-eventing', 'hibernate-hijacker' ]
    
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp",
            "**/demo/**"
    ]

    def author = "Kim A. Betti"
    def authorEmail = "kim@developer-b.com"
    def title = "MultiTenantCore - DEMO"
    def description = '''\\
Brief description of the plugin.
'''

    def documentation = "http://grails.org/plugin/multi-tenant-core"

    def doWithSpring = {
        
        currentTenant(CurrentTenantThreadLocal)
		
		// Tenant scope
		tenantScopeConfigurator(TenantScopeConfigurator) {
			currentTenant = ref("currentTenant")
		}
        
        tenantUtils(TenantUtils) {
            currentTenant = ref("currentTenant")
            sessionFactory = ref("sessionFactory")
        }
        
        tenantHibernateFilterEnabler(TenantHibernateFilterEnabler) {
            eventBroker = ref("eventBroker")
            currentTenant = ref("currentTenant")
        }

        // Inserts tenantId, makes sure that we're not 
        // loading other tenant's data and so on
        tenantHibernateEventListener(TenantHibernateEventListener) {
            currentTenant = ref("currentTenant")
        }
        
        // Enables the tenant filter for our domain classes
        tenantFilterConfigurator(TenantFilterConfigurator) {
            eventBroker = ref("eventBroker")
            grailsApplication = ref("grailsApplication")
            tenantHibernateEventListener = ref("tenantHibernateEventListener")
        }
		
		// Listens for new / removed tenants
		tenantDomainClassListener(TenantDomainClassListener) {
			eventBroker = ref("eventBroker")
			grailsApplication = ref("grailsApplication")
		}
		
		// Set per-tenant beans up in the custom tenant scope 
		tenantBeanFactoryPostProcessor(TenantBeanFactoryPostProcessor) {
			perTenantBeans = ConfigurationHolder.config?.multiTenant?.perTenantBeans ?: []
		}
		
    }
    
    def doWithWebDescriptor = { xml ->
        def contextParam = xml.'context-param'
        contextParam[contextParam.size() - 1] + {
            'filter' {
                'filter-name'('tenantFilter')
                'filter-class'(CurrentTenantFilter.name)
            }
        }

        def filter = xml.'filter'
        filter[filter.size() - 1] + {
            'filter-mapping' {
                'filter-name'('tenantFilter')
                'url-pattern'('/*')
				'dispatcher' 'REQUEST'
				'dispatcher' 'ERROR'
            }
        }
    }

    def doWithDynamicMethods = { ctx -> }
    def doWithApplicationContext = { applicationContext -> }
    def onChange = { event -> }
    def onConfigChange = { event -> }
    
}
