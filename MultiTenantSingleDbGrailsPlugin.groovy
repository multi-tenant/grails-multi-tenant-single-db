import grails.plugin.multitenant.singledb.MtSingleDbPluginSupport
import grails.util.Environment

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MultiTenantSingleDbGrailsPlugin {
    private Logger log = LoggerFactory.getLogger('grails.plugin.multiTenant.MultiTenantSingleDbPlugin')

    def version = "0.8.3"
    def grailsVersion = "1.3.5 > *"

    def loadAfter = [
        'hawk-eventing',
        'hibernate-hijacker',
        'controllers'
    ]

    def pluginExcludes = ["**/demo/**" ]

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
                def SecurityFilterPosition = classLoader.loadClass('org.codehaus.groovy.grails.plugins.springsecurity.SecurityFilterPosition')
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
