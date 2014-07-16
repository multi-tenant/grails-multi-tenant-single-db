package demo

/**
 * Not a multi tenant domain class. Mostly as a smoke test to
 * detect if any multi-tenant code affects regular domain classes.
 * @author Kim A. Betti
 */
class DemoCustomer {
    String name

    static mapping = {
        datasource 'secondary'
    }

}
