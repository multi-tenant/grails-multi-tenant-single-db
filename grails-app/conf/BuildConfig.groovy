grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

//
//grails.project.fork = [
//    // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
//    //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
//
//    // configure settings for the test-app JVM, uses the daemon by default
//    test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
//    // configure settings for the run-app JVM
//    run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
//    // configure settings for the run-war JVM
//    war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
//    // configure settings for the Console UI JVM
//    console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
//]

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
