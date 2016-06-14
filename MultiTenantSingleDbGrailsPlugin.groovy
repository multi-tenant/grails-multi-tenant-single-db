import grails.plugin.multitenant.singledb.MtSingleDbPluginSupport
import grails.util.Environment
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsApplication

@Log4j
class MultiTenantSingleDbGrailsPlugin {
    // the plugin version
    def version = "1.0"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.4 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            '**/demo/**',
            "grails-app/views/error.gsp",
    ]

    def loadAfter = [
            'hawk-eventing',
            'hibernate-hijacker',
            'controllers'
    ]

    // TODO Fill in these fields
    def title = "MultiTenant - SingleDB" // Headline display name of the plugin
    def author = "Sandeep Poonia"
    def authorEmail = "sandeep.poonia.90@gmail.com"
    def description = '''\
Multi tenant setup focused on single database mode
'''

    // URL to the plugin's documentation
    def documentation = "https://github.com/spoonia/grails-multi-tenant-single-db"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
    def issueManagement = [system: "github", url: "https://github.com/spoonia/grails-multi-tenant-single-db/issues"]

    // Online location of the plugin's browseable source code.
    def scm = [url: "https://github.com/spoonia/grails-multi-tenant-single-db"]

    // make sure the filter chain filter is after the Grails filter
    def getWebXmlFilterOrder() {
        log.debug("Start getWebXmlFilterOrder")
        def filterMap = [:]
        try {
            def classLoader = new GroovyClassLoader(getClass().getClassLoader())
            def slurper = new ConfigSlurper(Environment.getCurrent().getName())
            def config = slurper.parse(classLoader.loadClass(GrailsApplication.CONFIG_CLASS))

            if (config.multiTenant.resolveTenantBeforeLogin) {
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

    def doWithWebDescriptor = { xml ->
        MtSingleDbPluginSupport.doWithWebDescriptor xml
    }

    def doWithSpring = {
        MtSingleDbPluginSupport.doWithSpring.delegate = delegate
        MtSingleDbPluginSupport.doWithSpring application
    }

    def doWithDynamicMethods = { ctx ->
        MtSingleDbPluginSupport.doWithDynamicMethods.delegate = delegate
        MtSingleDbPluginSupport.doWithDynamicMethods ctx, application
    }

    def doWithApplicationContext = { ctx ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
