import grails.plugin.multitenant.singledb.MtSingleDbPluginSupport

class MultiTenantSingleDbGrailsPlugin {

    def version = "0.8.2"
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
	
	def license = "APACHE"
	def developers = [
	        [ name: "Steve Ronderos", email: "steve.ronderos@gmail.com" ]
		]
	def issueManagement = [ system: "github", url: "https://github.com/multi-tenant/grails-multi-tenant-single-db/issues" ]
	def scm = [ url: "https://github.com/multi-tenant/grails-multi-tenant-single-db" ]
	
    // make sure the filter chain filter is after the Grails filter
    	def getWebXmlFilterOrder() {
    		def FilterManager = getClass().getClassLoader().loadClass('grails.plugin.webxml.FilterManager')
    		[tenantFilter: FilterManager.SITEMESH_POSITION - 100]
    	}

    def doWithSpring = MtSingleDbPluginSupport.doWithSpring
    def doWithDynamicMethods = MtSingleDbPluginSupport.doWithDynamicMethods
    def doWithWebDescriptor = MtSingleDbPluginSupport.doWithWebDescriptor

    def doWithApplicationContext = { applicationContext -> }
    def onChange = { event -> }
    def onConfigChange = { event -> }
    
}
