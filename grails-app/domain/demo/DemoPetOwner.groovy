package demo

import grails.plugin.multitenant.core.annotation.MultiTenant

@MultiTenant
class DemoPetOwner {

    static hasMany = [ pets: DemoAnimal ]

    static mapping = {
        datasource 'secondary'
    }

    String name
}
