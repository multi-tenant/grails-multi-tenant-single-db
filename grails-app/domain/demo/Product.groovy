package demo

import grails.plugin.multitenant.core.annotation.MultiTenant

@MultiTenant
class Product {
	
	String name

    static constraints = {
    }
	
}
