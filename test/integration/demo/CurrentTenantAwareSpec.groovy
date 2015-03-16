package demo

import grails.plugin.spock.IntegrationSpec

/**
 * @author Steve Ronderos
 */
class CurrentTenantAwareSpec extends IntegrationSpec {

    def demoStore

    def "current tenant is injected into the service"() {
        expect:
        demoStore != null
        demoStore.currentTenant != null
    }
}