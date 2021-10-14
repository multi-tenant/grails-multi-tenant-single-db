grails.project.work.dir = 'target'

Map<String, String> ENV = System.getenv();
grails.project.work.dir = 'target'
String mvnRepoHost = ENV['MVN_REPO_HOST']
String mvnRepoUser = ENV['MVN_REPO_USER']
String mvnRepoPassword = ENV['MVN_REPO_PASSWORD']

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		mavenRepo 'https://repo1.maven.org/maven2/'
		mavenRepo 'https://grails.jfrog.io/grails/plugins'
		mavenLocal()
	}

	dependencies {
		test('org.spockframework:spock-grails-support:0.7-groovy-2.0') {
			excludes 'groovy', 'groovy-all'
		}
	}
	credentials {
		realm = ENV['MVN_REPO_REALM']
		host = mvnRepoHost
		username = mvnRepoUser
		password = mvnRepoPassword
	}
	plugins {
		build ':release:2.2.1', ':rest-client-builder:1.0.3', {
			export = false
		}

		provided ':webxml:1.4.1'

		compile(":hibernate:3.6.10.16") // or ":hibernate4:4.3.5.4"

		compile(':hawk-eventing:0.5.1') {
			excludes 'svn'
		}

		compile(':hibernate-hijacker:0.9.0-SNAPSHOT') {
			excludes 'svn'
		}

		test(':spock:0.7') {
			excludes 'svn', 'spock-grails-support'
			export = false
		}
	}
}

grails.project.repos.releases.url = ENV['MVN_REPO_REPOSITORIES_URL_PLUGINS_RELEASE']
grails.project.repos.releases.username = mvnRepoUser
grails.project.repos.releases.password = mvnRepoPassword

grails.project.repos.snapshots.url = ENV['MVN_REPO_REPOSITORIES_URL_PLUGINS_SNAPSHOT']
grails.project.repos.snapshots.username = mvnRepoUser
grails.project.repos.snapshots.password = mvnRepoPassword

grails.project.repos.default = 'releases'
