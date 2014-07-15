grails.project.work.dir = 'target'

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
                mavenLocal()
	}

	dependencies {
		test('org.spockframework:spock-grails-support:0.7-groovy-2.0') {
			excludes 'groovy', 'groovy-all'
		}
	}

	plugins {
		build ':release:2.2.1', ':rest-client-builder:1.0.3', {
			export = false
		}

		provided ':webxml:1.4.1'

		compile(":hibernate:$grailsVersion") { export = false }

		compile(':hawk-eventing:0.5.1') {
			excludes 'svn'
		}

		compile(':hibernate-hijacker:0.8.1') {
			excludes 'svn'
		}

		test(':spock:0.7') {
			excludes 'svn', 'spock-grails-support'
			export = false
		}
	}
}
