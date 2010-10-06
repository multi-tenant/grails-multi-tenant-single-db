import org.codehaus.groovy.grails.orm.hibernate.HibernateEventListeners
import grails.util.Environment

import grails.plugin.multitenant.core.*
import grails.plugin.multitenant.core.filter.CurrentTenantFilter
import grails.plugin.multitenant.core.event.*
import grails.plugin.multitenant.core.hibernate.*

class MultiTenantCoreGrailsPlugin {
    
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.5 > *"
    
    // the other plugins this plugin depends on
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
        }
        
        hibernateSessionConsumer(HibernateSessionConsumer) {
            eventBroker = ref("eventBroker")
            currentTenant = ref("currentTenant")
        }

        preInsertListener(HibernateInsertConsumer) {
            currentTenant = ref("currentTenant")
        }
        
        hibernateEventListeners(HibernateEventListeners) {
            listenerMap = [ 'pre-insert': preInsertListener ]
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
