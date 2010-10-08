import grails.util.Environment

import grails.plugin.multitenant.core.*
import grails.plugin.multitenant.core.util.TenantUtils
import grails.plugin.multitenant.core.filter.CurrentTenantFilter
import grails.plugin.multitenant.core.hibernate.event.TenantHibernateEventListener
import grails.plugin.multitenant.core.hibernate.event.TenantHibernateFilterEnabler
import grails.plugin.multitenant.core.hibernate.*

class MultiTenantCoreGrailsPlugin {
    
    def version = "0.1"
    def grailsVersion = "1.3.5 > *"
    
    // the other plugins this plugin depends on
    // This caused random problems with Grails, looks like a concurrency issue
//    def dependsOn = [
//        'eventing': '0 > *',
//        'hibernate-hijacker': '0 > *'    
//    ]
    
    def loadAfter = [ 'eventing', 'hibernate-hijacker' ]
    
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp",
            "**/demo/**"
    ]

    def author = "Your name"
    def authorEmail = ""
    def title = "Plugin summary/headline"
    def description = '''\\
Brief description of the plugin.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/multi-tenant-core"

    

    def doWithSpring = {
        
        currentTenant(CurrentTenantThreadLocal)
        
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
            }
        }
    }

    def doWithDynamicMethods = { ctx -> }
    def doWithApplicationContext = { applicationContext -> }
    def onChange = { event -> }
    def onConfigChange = { event -> }
    
}
