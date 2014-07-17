/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package demo
import grails.plugin.multitenant.core.Tenant

import grails.test.mixin.TestFor
import spock.lang.Specification
/**
 *
 * @author Omasirichukwu
 */
@TestFor(AnotherDemoService)
class AnotherDemoServiceSpec extends Specification {
     def anotherDemoService
     
     def "tenant scope is working in service"() {
        given: "each tenant set a val on service"
            Tenant.withTenantId 1, {
                assert "none" == anotherDemoService.touchedByTenant
                anotherDemoService.touchedByTenant = "Tenant-1"
            }

            Tenant.withTenantId 2, {
                assert "none" == anotherDemoService.touchedByTenant
                anotherDemoService.touchedByTenant = "Tenant-2"
            }

        expect: "we should get values that were set"
            Tenant.withTenantId 1, {
                println anotherDemoService.touchedByTenant
                "Tenant-1" == anotherDemoService.touchedByTenant
            }
            Tenant.withTenantId 2, {
                println anotherDemoService.touchedByTenant
                "Tenant-2" == anotherDemoService.touchedByTenant
            }
    }
	
}

