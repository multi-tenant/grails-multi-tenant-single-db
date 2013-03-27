grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = '.grails'

grails.project.dependency.resolution = {

    // inherit Grails' default dependencies
    inherits("global") {
        // excludes 'ehcache'
    }

    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {

        mavenLocal()
        mavenCentral()

        grailsPlugins()
        grailsHome()
        grailsCentral()

    }

    dependencies {
    }
    
    plugins {
		build(':release:2.0.2'){ export = false }
		build(":tomcat:$grailsVersion") { export = false }
		compile(":hibernate:$grailsVersion") { export = false }
        compile ":rest-client-builder:1.0.2"

		provided ':webxml:1.4.1'
		
		compile(':hawk-eventing:0.5.1'){
			excludes 'svn'
		}
		
		compile(':hibernate-hijacker:0.8.1'){
			excludes 'svn'
		}

		test(':spock:0.6'){
			excludes 'svn'
			export = false
		}
    }
    
}
