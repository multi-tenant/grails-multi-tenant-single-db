import grails.plugin.multitenant.singledb.MtSingleDbPluginSupport

class MultiTenantSingleDbGrailsPlugin {

    def version = "0.8.1"
    def grailsVersion = "1.3.5 > *"

    def dependsOn = [:] // does not play well with Maven repositories

    def loadAfter = [
        'hawk-eventing',
        'hibernate-hijacker',
        'controllers'
    ]

    def pluginExcludes = [ "grails-app/views/error.gsp", "**/demo/**" ]

    def author = "Kim A. Betti"
    def authorEmail = "kim@developer-b.com"
    def title = "MultiTenant - SingleDB"
    def description = "Multi tenant setup focused on single database mode"

    def documentation = "https://github.com/multi-tenant/grails-multi-tenant-single-db"

    def doWithSpring = MtSingleDbPluginSupport.doWithSpring
    def doWithDynamicMethods = MtSingleDbPluginSupport.doWithDynamicMethods
    def doWithWebDescriptor = MtSingleDbPluginSupport.doWithWebDescriptor

    def doWithApplicationContext = { applicationContext -> }
    def onChange = { event -> }
    def onConfigChange = { event -> }
    
}
