grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

//grails.plugin.location."eventing" = "../grails-eventing"
//grails.plugin.location."hibernate-hijacker" = "../hibernate-hijacker"

grails.project.dependency.resolution = {

    // inherit Grails' default dependencies
    inherits("global") {
    }
    
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
		
		mavenLocal()
        mavenCentral()
		
        grailsPlugins()
        grailsHome()
        grailsCentral()

        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    
    dependencies {
		plugins {
			compile "plugins.utilities:hawk-eventing:0.2"
			compile "plugins.multitenant:hibernate-hijacker:0.2.3"
		}
    }
    
}