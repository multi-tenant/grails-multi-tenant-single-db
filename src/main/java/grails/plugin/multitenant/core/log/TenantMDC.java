package grails.plugin.multitenant.core.log;

import org.slf4j.MDC;

import grails.plugin.multitenant.core.CurrentTenant;
import grails.plugins.hawkeventing.Event;
import grails.plugins.hawkeventing.annotation.Consuming;
import grails.plugins.hawkeventing.annotation.HawkEventConsumer;

/**
 * MDC / Mapped diagnostic context.
 * 
 * @see http://logback.qos.ch/manual/mdc.html
 * @see http://www.slf4j.org/api/org/slf4j/MDC.html
 * @see http://blog.oio.de/2010/11/23/usage-tracking-with-grails/
 * @author Kim A. Betti
 */
@HawkEventConsumer
public class TenantMDC {

    @Consuming(CurrentTenant.TENANT_AFTER_CHANGE_EVENT)
    public void currentTenantUpdated(Event event) {
        Integer tenantId = (Integer) event.getPayload();
        String tenant = (tenantId == null) ? "no-tenant" : "tenant-" + tenantId;
        MDC.put("tenant", tenant);
    }

}