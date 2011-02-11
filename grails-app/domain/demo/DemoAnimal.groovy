package demo

import grails.plugin.multitenant.core.annotation.MultiTenant


@MultiTenant
class DemoAnimal {

    String name

    static constraints = {
    }

    String toString() {
        "Animal[name: $name]"
    }
}
