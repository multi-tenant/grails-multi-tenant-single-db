grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {

	// inherit Grails' default dependencies
	inherits("global") {
	}
	
	log 'warn'

	repositories {
			grailsCentral()
			mavenLocal()
			mavenCentral()
	}
	
	dependencies {
	}

	plugins {
		build(":release:3.0.1",":rest-client-builder:1.0.3") {
			export = false
		}
		
		provided ':webxml:1.4.1'

		compile(":hibernate:3.6.10.15") { export = false }
		
		compile ':cache:1.1.6'
		
		compile(':hawk-eventing:0.5.1') {
			excludes 'svn'
		}

		compile(':hibernate-hijacker:0.8.1') {
			excludes 'svn'
		}		
	}
}
