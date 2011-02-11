package grails.plugin.multitenant.singledb.annotation;

import java.lang.annotation.*;
import org.codehaus.groovy.transform.GroovyASTTransformationClass;

/**
 * Domain classes annotated with this plugin
 * will get a tenantId property added during compilation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@GroovyASTTransformationClass("grails.plugin.multitenant.singledb.ast.MultiTenantAST")
public @interface MultiTenant {
}