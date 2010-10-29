package grails.plugin.multitenant.core.annotation;

import java.lang.annotation.*;
import org.codehaus.groovy.transform.GroovyASTTransformationClass;

/**
 * Domain classes annotated with this plugin
 * will get a tenantId property added during compilation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@GroovyASTTransformationClass("grails.plugin.multitenant.core.ast.MultiTenantAST")
public @interface MultiTenant {
}