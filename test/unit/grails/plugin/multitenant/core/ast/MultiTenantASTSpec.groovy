package grails.plugin.multitenant.core.ast

import grails.plugin.multitenant.core.MultiTenantDomainClass
import grails.plugin.multitenant.core.annotation.MultiTenant
import grails.test.mixin.TestFor
import spock.lang.Specification

import java.lang.reflect.Field

/**
 * @author Kim A. Betti
 */
class MultiTenantASTSpec extends Specification {

    def "should have a tenantId field"() {
        expect:
        SampleDomainClass.declaredFields.find { Field field ->
            field.name == "tenantId"
            field.type == Integer
        }
    }

    def "non multi-tenant classes should not have this field"() {
        expect:
        !NotADomainClass.declaredFields.find { Field field ->
            field.name == "tenantId"
        }
    }

    def "all classes annotated with MultiTenant should implement MultiTenantDomainClass"() {
        expect:
        def sampleInstance = new SampleDomainClass(name: "test")
        sampleInstance instanceof MultiTenantDomainClass
    }

    def "non domain classes should not be equiped with this interface"() {
        expect:
        def sampleInstance = new NotADomainClass(name: "test")
        !(sampleInstance instanceof MultiTenantDomainClass)
    }
}

@MultiTenant
class SampleDomainClass {
    String name
}

class NotADomainClass {
    String name
}
