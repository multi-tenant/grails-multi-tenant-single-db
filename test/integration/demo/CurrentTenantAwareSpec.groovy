package demo

import spock.lang.Specification

/**
 * @author Steve Ronderos
 */
class CurrentTenantAwareSpec extends Specification {

    def demoStore

    def "current tenant is injected into the service"() {
        expect:
        demoStore != null
        demoStore.currentTenant != null
    }
}