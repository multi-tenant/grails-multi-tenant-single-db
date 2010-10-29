package grails.plugin.multitenant.core.ast;

import grails.plugin.multitenant.core.hibernate.TenantFilterCfg;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    
    private static Log log = LogFactory.getLog(MultiTenantAST.class);
	
	@Override
    public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
    	for (ClassNode classNode : getClassAstNodes(astNodes)) {
    		log.debug("Processing " + classNode.getName());
    		if (isMissingTenantId(classNode)) {
    			addTenantProperty(classNode);
    		}
    	}
    }
    
    private List<ClassNode> getClassAstNodes(ASTNode[] astNodes) {
    	List<ClassNode> classAstNodes = new ArrayList<ClassNode>();
    	for (ASTNode astNode : astNodes) {
            if (astNode instanceof ClassNode) {
            	ClassNode classAstNode = (ClassNode) astNode;
            	classAstNodes.add(classAstNode);
            }
    	}
    	
    	return classAstNodes;
    }
    
    private boolean isMissingTenantId(ClassNode node) {
    	return !GrailsASTUtils.hasOrInheritsProperty(node, "tenantId");
    }
    
    private void addTenantProperty(ClassNode node) {
        log.debug("Adding tenant id property to " + node.getName());
    	ClassNode integerType = new ClassNode(Integer.class);
    	ConstantExpression defaultValue = new ConstantExpression(0);
    	Statement getterBlock = null;
    	Statement setterBlock = null;
    	node.addProperty(TenantFilterCfg.TENANT_ID_FIELD_NAME, Modifier.PUBLIC, integerType, 
    			defaultValue, getterBlock, setterBlock);
    }
    
}