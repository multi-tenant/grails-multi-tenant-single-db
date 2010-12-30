package demo

import grails.plugin.multitenant.core.annotation.MultiTenant;

/**
 * So far classes inheriting from a @MultiTenant annotated
 * class still have to add the same annotation. 
 * I haven't figured out why this is yet. 
 */
@MultiTenant
class DemoDog extends DemoAnimal {
	
	int catsKilled = 0

    static constraints = {
    }
	
}
