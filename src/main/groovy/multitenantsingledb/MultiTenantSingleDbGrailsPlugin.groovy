package multitenantsingledb

import grails.plugin.multitenant.singledb.MtSingleDbPluginSupport
import grails.util.Environment

import grails.core.GrailsApplication
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import grails.plugins.*

class MultiTenantSingleDbGrailsPlugin extends Plugin {
 private Logger log = LoggerFactory.getLogger('grails.plugin.multiTenant.MultiTenantSingleDbPlugin')

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.1.1 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp",
		"**/demo/**"
    ]
def loadAfter = [
        'hawk-eventing',
        'hibernate-hijacker',
        'controllers'
    ]

    def author = "Kim A. Betti"
    def authorEmail = "kim@developer-b.com"
    def title = "MultiTenant - SingleDB"
    def description = "Multi tenant setup focused on single database mode"

    def documentation = "https://github.com/multi-tenant/grails-multi-tenant-single-db"

    def license = "APACHE"
    def developers = [
        [ name: "Steve Ronderos", email: "steve.ronderos@gmail.com" ]
    ]
    def issueManagement = [ system: "github", url: "https://github.com/multi-tenant/grails-multi-tenant-single-db/issues" ]
    def scm = [ url: "https://github.com/multi-tenant/grails-multi-tenant-single-db" ]

    // make sure the filter chain filter is after the Grails filter
    def getWebXmlFilterOrder() {
        log.debug("Start getWebXmlFilterOrder")
        def filterMap = [:]
        try {
            def classLoader = new GroovyClassLoader(getClass().getClassLoader())
            def slurper = new ConfigSlurper(Environment.getCurrent().getName())
            def config = slurper.parse(classLoader.loadClass(GrailsApplication.CONFIG_CLASS))

            if(config.multiTenant.resolveTenantBeforeLogin) {
                def SecurityFilterPosition = classLoader.loadClass('org.grails.plugins.springsecurity.SecurityFilterPosition')
                log.debug("set WebXmlFilterOrder before login")
                filterMap = [tenantFilter: SecurityFilterPosition.FORM_LOGIN_FILTER.order - 100]
            } else {
                def FilterManager = classLoader.loadClass('grails.plugin.webxml.FilterManager')
                log.debug("set WebXmlFilterOrder after login")
                filterMap = [tenantFilter: FilterManager.SITEMESH_POSITION - 100]
            }
        } catch (ClassNotFoundException e) {
            log.warn "Could not determine desired tenantFilter position."
        }
        return filterMap
    }

    def doWithSpring = {
		 MtSingleDbPluginSupport.doWithSpring.delegate = delegate
		 MtSingleDbPluginSupport.doWithSpring application
    }

    def doWithDynamicMethods = { ctx ->
		 MtSingleDbPluginSupport.doWithDynamicMethods.delegate = delegate
		 MtSingleDbPluginSupport.doWithDynamicMethods ctx, application
    }

    def doWithWebDescriptor = MtSingleDbPluginSupport.doWithWebDescriptor
}
