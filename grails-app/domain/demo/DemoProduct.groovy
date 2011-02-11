package demo

import grails.plugin.multitenant.singledb.annotation.MultiTenant

@MultiTenant
class DemoProduct {
	
	String name

    static constraints = {
		name unique: 'tenantId', blank: false
    }
	
}