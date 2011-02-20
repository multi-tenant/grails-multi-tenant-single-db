import grails.plugin.multitenant.core.CurrentTenant;
import grails.plugin.multitenant.core.CurrentTenantThreadLocal;
import grails.plugin.multitenant.core.MultiTenantContext;
import grails.plugin.multitenant.core.MultiTenantService;
import grails.plugin.multitenant.core.Tenant;
import grails.plugin.multitenant.core.servlet.CurrentTenantServletFilter;
import grails.plugin.multitenant.singledb.hibernate.TenantHibernateFilterConfigurator;
import grails.plugin.multitenant.core.hibernate.event.TenantDomainClassListener;
import grails.plugin.multitenant.core.hibernate.event.TenantHibernateEventListener;
import grails.plugin.multitenant.singledb.hibernate.event.TenantHibernateFilterEnabler;
import grails.plugin.multitenant.core.spring.ConfiguredTenantScopedBeanProcessor;
import grails.plugin.multitenant.core.spring.TenantScopeConfigurator;
import grails.plugin.multitenant.core.spring.TenantScope
import org.springframework.beans.factory.config.CustomScopeConfigurer
import grails.util.Environment
import org.codehaus.groovy.grails.commons.spring.GrailsWebApplicationContext
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class MultiTenantSingleDbGrailsPlugin {

    def version = "0.4.3"
    def grailsVersion = "1.3.5 > *"

    def dependsOn = [:] // does not play well with Maven repositories

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
Multi tenant setup focused on single database mode.
'''

    def documentation = "https://github.com/multi-tenant/grails-multi-tenant-single-db"

    def doWithSpring = {

        // Default CurrentTenant implementation storing
        // the current tenant id in a ThreadLocal variable. 
        currentTenant(CurrentTenantThreadLocal) {
            eventBroker = ref("eventBroker")
        }

        // A custom Spring scope for beans. 
        tenantScope(TenantScope) {
            currentTenant = ref("currentTenant")
        }

        // Set per-tenant beans up in the custom tenant scope
        configuredTenantBeanProcessor(ConfiguredTenantScopedBeanProcessor) {
            perTenantBeans = ConfigurationHolder.config?.multiTenant?.perTenantBeans ?: []
        }
        
        // Responsible for registering the custom 'tenant' scope with Spring.
        tenantScopeConfigurer(CustomScopeConfigurer) {
            scopes = [ tenant: ref("tenantScope") ]
        }
        
        // Listens for new Hibernate sessions and enables the 
        // multi-tenant filter with the current tenant id.  
        tenantHibernateFilterEnabler(TenantHibernateFilterEnabler) {
            currentTenant = ref("currentTenant")
            sessionFactory = ref("sessionFactory")
        }

        // Keeps track of multi-tenant related classes. 
        // Was originally introduced to deprecate TenantUtils and
        // make other beans independent of grailsApplication. 
        multiTenantContext(MultiTenantContext) {
            grailsApplication = ref("grailsApplication")
        }
        
        // Inserts tenantId, makes sure that we're not
        // loading other tenant's data and so on
        tenantHibernateEventListener(TenantHibernateEventListener) {
            currentTenant = ref("currentTenant")
        }

        // Enables the tenant filter for our domain classes
        tenantFilterConfigurator(TenantHibernateFilterConfigurator) {
            eventBroker = ref("eventBroker")
            multiTenantContext = ref("multiTenantContext")
            tenantHibernateEventListener = ref("tenantHibernateEventListener")
        }

        // Listens for new, removed and updated tenants and broadcasts
        // the information using Hawk Eventing making it easier to
        // listen in on these events. 
        tenantDomainClassListener(TenantDomainClassListener) {
            eventBroker = ref("eventBroker")
            multiTenantContext = ref("multiTenantContext")
        }

    }

    def doWithDynamicMethods = { ctx ->
        MultiTenantService mtService = ctx.getBean("multiTenantService")
        MultiTenantContext mtCtx = ctx.getBean("multiTenantContext")
        Class tenantClass = mtCtx.getTenantClass()
        
        // TODO: Should we print a warning if we don't find a tenant class?
        if (tenantClass) {
            createDoWithTenant(tenantClass, mtService)
            createDoWithTenantIdMethod(tenantClass, mtService)
        } 
        
        createDoWithTenantIdMethod(Tenant, mtService)
    }
    
    protected createDoWithTenant(Class tenantClass, MultiTenantService mtService) {
        tenantClass.metaClass.withThisTenant = { Closure closure ->
            Integer tenantId = getTenantId()
            mtService.doWithTenantId(tenantId, closure)
        }
    }
    
    protected createDoWithTenantIdMethod(Class tenantClass, MultiTenantService mtService) {
        tenantClass.metaClass.'static'.withTenantId = { Integer tenantId, Closure closure ->
            mtService.doWithTenantId(tenantId, closure)
        }
    }
    
    def doWithWebDescriptor = { xml ->
        def contextParam = xml.'context-param'
        contextParam[contextParam.size() - 1] + {
            'filter' {
                'filter-name'('tenantFilter')
                'filter-class'(CurrentTenantServletFilter.name)
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

    def doWithApplicationContext = { applicationContext -> }
    def onChange = { event -> }
    def onConfigChange = { event -> }
    
}