package demo

import org.springframework.context.annotation.Scope

/**
 * Scope configured using Grails convention.
 * @author Kim A. Betti email:kim@developer-b.com
 */
class AnotherDemoService {

    static transactional = false
    static scope = "prototype"

    def touchedByTenant = "none"
}
