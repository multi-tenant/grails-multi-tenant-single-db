package grails.plugin.multitenant.core.ast;

import grails.plugin.multitenant.core.MultiTenantDomainClass;

import java.lang.reflect.Modifier;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.grails.compiler.injection.GrailsASTUtils;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

/**
 * Adds a tenantId property to domain classes annotated MultiTenant.
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class MultiTenantAST implements ASTTransformation {

    public final static String TENANT_ID_FIELD_NAME = "tenantId";

    // TODO: ConstantExpression.NULL would be better, but that leads to all sorts of crazy problems
    // with default GORM constraints. It looks like it will become easier to hook into this in Grails 1.4:
    // https://github.com/grails/grails-core/blob/master/grails-core/src/main/groovy/org/codehaus/groovy/grails/validation/ConstraintsEvaluatorFactoryBean.java
    public final static Integer NO_TENANT_VALUE = Integer.MIN_VALUE;

    @Override
    public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        ClassNode annotatedClassNode = (ClassNode) astNodes[1];
        addTenantPropertyIfNecessary(annotatedClassNode);
        implementInterface(annotatedClassNode, MultiTenantDomainClass.class);
    }

    private void addTenantPropertyIfNecessary(ClassNode classNode) {
        if (!hasTenantIdProperty(classNode)) {
            addTenantProperty(classNode);
        }
    }

    private boolean hasTenantIdProperty(ClassNode node) {
        return GrailsASTUtils.hasOrInheritsProperty(node, "tenantId");
    }

    private void addTenantProperty(ClassNode node) {
        ClassNode integerType = new ClassNode(Integer.class);
        ConstantExpression defaultValue = new ConstantExpression(NO_TENANT_VALUE);
        Statement getterBlock = null;
        Statement setterBlock = null;
        node.addProperty(TENANT_ID_FIELD_NAME, Modifier.PUBLIC,
                integerType, defaultValue, getterBlock, setterBlock);
    }

    private void implementInterface(ClassNode node, Class<?> interf) {
        ClassNode interfaceClassNode = new ClassNode(interf);
        if (!node.implementsInterface(interfaceClassNode)) {
            node.addInterface(interfaceClassNode);
        }
    }

}