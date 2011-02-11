package grails.plugin.multitenant.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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