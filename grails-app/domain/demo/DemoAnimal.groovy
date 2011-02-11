package demo

import grails.plugin.multitenant.singledb.annotation.MultiTenant;

@MultiTenant
class DemoAnimal {
	
	String name

    static constraints = {
    }
	
	String toString() {
		"Animal[name: $name]"
	}
	
}
