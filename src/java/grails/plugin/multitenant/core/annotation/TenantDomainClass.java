package grails.plugin.multitenant.core.annotation;

import java.lang.annotation.*;

/**
 * Annotate the class containing tenant information with this.
 * This annotation should only be applied to one domain class per project.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TenantDomainClass {
}