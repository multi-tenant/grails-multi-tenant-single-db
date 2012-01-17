grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

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

        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }

    dependencies {
    }
    
    plugins {
		build(':release:1.0.1'){ export = false }
		build(':svn:1.0.2') { export = false }
		build(":tomcat:$grailsVersion") { export = false }
		
		compile(":hibernate:$grailsVersion") { export = false }

		provided ':webxml:1.4'
		compile(':hawk-eventing:0.5.1'){
			excludes 'svn'
		}
		
		compile(':hibernate-hijacker:0.8.1'){
			excludes 'svn'
		}

		test(':spock:0.6-SNAPSHOT'){
			excludes 'svn'
		}
    }
    
}
