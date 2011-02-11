import grails.plugin.multitenant.core.CurrentTenantThreadLocal;
import grails.plugin.multitenant.core.filter.CurrentTenantFilter;
import grails.plugin.multitenant.singledb.hibernate.TenantFilterConfigurator;
import grails.plugin.multitenant.core.hibernate.event.TenantDomainClassListener;
import grails.plugin.multitenant.core.hibernate.event.TenantHibernateEventListener;
import grails.plugin.multitenant.singledb.hibernate.event.TenantHibernateFilterEnabler;
import grails.plugin.multitenant.core.spring.TenantBeanFactoryPostProcessor;
import grails.plugin.multitenant.core.spring.TenantScopeConfigurator;
import grails.plugin.multitenant.core.util.TenantUtils;
import grails.util.Environment

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class MultiTenantSingleDbGrailsPlugin {

    def version = "0.3"
    def grailsVersion = "1.3.5 > *"

    def dependsOn = [
        hawkEventing: '0.4.1 > *',
        hibernateHijacker: '0.2.6 > *'
    ]

    def loadAfter = [
        'hawk-eventing',
        'hibernate-hijacker'
    ]

    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp",
        "**/demo/**"
    ]

    def author = "Kim A. Betti"
    def authorEmail = "kim@developer-b.com"
    def title = "MultiTenant - SingleDB"
    def description = '''\\
Multi tenant setup focused on single db mode
'''

    def documentation = "https://github.com/multi-tenant/grails-multi-tenant-single-db"

    def doWithSpring = {

        currentTenant(CurrentTenantThreadLocal) {
            eventBroker = ref("eventBroker")
        }

        // Tenant scope
        tenantScopeConfigurator(TenantScopeConfigurator) { 
            currentTenant = ref("currentTenant")
        }

        tenantUtils(TenantUtils) {
            currentTenant = ref("currentTenant")
            sessionFactory = ref("sessionFactory")
        }

        tenantHibernateFilterEnabler(TenantHibernateFilterEnabler) {
            currentTenant = ref("currentTenant")
            sessionFactory = ref("sessionFactory")
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