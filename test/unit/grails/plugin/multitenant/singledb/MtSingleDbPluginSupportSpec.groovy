package grails.plugin.multitenant.singledb

import demo.DemoTenant;
import spock.util.mop.ConfineMetaClassChanges;
import grails.plugin.multitenant.core.MultiTenantService;
import grails.plugin.multitenant.core.Tenant;
import grails.plugin.multitenant.core.exception.TenantException;
import grails.plugin.spock.UnitSpec;

/**
 * 
 * @author Kim A. Betti
 */
class MtSingleDbPluginSupportSpec extends UnitSpec {

    def "add withTenantId method"() {
        given:
        MultiTenantService mockedMtService = Mock()
        MetaTenant tenant = new MetaTenant()
        
        when: "we add a withThisTenant method to the class"
        MtSingleDbPluginSupport.createWithThisTenantMethod(MetaTenant, mockedMtService)
        
        and: "and invoke it"
        tenant.withThisTenant(null)
        
        then: "the call is delegated to the mocked mt service"
        1 * mockedMtService.doWithTenantId(123, _)
    }
    
    def "add a static withTenantId method"() {
        given:
        MultiTenantService mockedMtService = Mock()
        
        when: "we add a withThisTenant method to the class"
        MtSingleDbPluginSupport.createWithTenantIdMethod(MetaTenant, mockedMtService)
        
        and: "and invoke it"
        MetaTenant.withTenantId(321, null)
        
        then: "the call is delegated to the mocked mt service"
        1 * mockedMtService.doWithTenantId(321, _)
    }
    
    def "add a static withoutTenantRestriction method"() {
        given:
        MultiTenantService mockedMtService = Mock()
        
        when: "we add a withThisTenant method to the class"
        MtSingleDbPluginSupport.createWithoutTenantRestrictionMethod(MetaTenant, mockedMtService)
        
        and: "and invoke it"
        MetaTenant.withoutTenantRestriction(null)
        
        then: "the call is delegated to the mocked mt service"
        1 * mockedMtService.doWithTenantId(null, _)
    }
    
    def "calling withThisTenant on a transient tenant instance should trigger an exception"() {
        given:
        MetaTenant transientTenant = new MetaTenant()
        transientTenant.tenantId = null
        
        when:
        MtSingleDbPluginSupport.createWithThisTenantMethod(MetaTenant, null)
        
        and:
        transientTenant.withThisTenant {
            throw new Exception("This should never be executed")
        }
        
        then:
        TenantException tex = thrown()
    }
    
    private class MetaTenant implements Tenant {
        Integer id
        Integer tenantId = 123
        Integer tenantId() { tenantId }
    }
    
}
