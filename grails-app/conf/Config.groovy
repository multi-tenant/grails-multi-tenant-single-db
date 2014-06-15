log4j = {
	error 'org.codehaus.groovy.grails',
	      'org.springframework',
	      'org.hibernate',
	      'net.sf.ehcache.hibernate'
	info    'grails.app'

	debug   'grails.plugin.multitenant'
}

multiTenant {
    perTenantBeans = [ "demoService" ]
    tenantClass = demo.DemoTenant
}

grails.doc.authors="Kim A. Betti"
grails.doc.license="Apache 2.0"
grails.doc.copyright=""
grails.doc.footer="Have a nice day!"

