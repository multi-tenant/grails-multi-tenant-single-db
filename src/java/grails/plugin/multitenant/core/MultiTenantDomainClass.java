package grails.plugin.multitenant.core;

/**
 * All multi tenant domain classes should implement this interface.
 * If a @MultiTenant annotated domain class doesn't implement this
 * interface it will be added by an AST transformation.
 *
 * There is no need to add the methods defined by this interface
 * manually as a tenantId property is added to all @MultiTenant annotated
 * domain classes. Groovy will create the get and set method for us.
 *
 * @author Kim A. Betti
 */
public interface MultiTenantDomainClass {

    Long getTenantId();

    void setTenantId(Long tenantId);

}
