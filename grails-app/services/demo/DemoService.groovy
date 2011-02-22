package demo

import org.springframework.context.annotation.Scope;

/**
 * This Spring bean is configured as per-tenant and used to 
 * verify that each tenant will see a different instance.
 * @author Kim A. Betti <kim@developer-b.com>
 */
class DemoService {

    static transactional = false

    def touchedByTenant = "none"
    
}