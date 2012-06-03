package grails.plugin.multitenant.core.ast;

import grails.plugin.multitenant.core.MultiTenantDomainClass;

// import org.codehaus.groovy.ast.ASTNode;
// import org.codehaus.groovy.ast.ClassNode;
// import org.codehaus.groovy.ast.expr.ConstantExpression;
// import org.codehaus.groovy.ast.stmt.Statement;
// import org.codehaus.groovy.control.CompilePhase;
// import org.codehaus.groovy.control.SourceUnit;
// import org.codehaus.groovy.grails.compiler.injection.GrailsASTUtils;
// import org.codehaus.groovy.transform.ASTTransformation;
// import org.codehaus.groovy.transform.GroovyASTTransformation;
//---------------------
import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;

import java.io.*;
import java.util.*;
import java.lang.reflect.Modifier;

import org.codehaus.groovy.grails.compiler.injection.GrailsASTUtils;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.expr.ListExpression;

// import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
// import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.codehaus.groovy.ast.MethodNode.*;

/**
 * Adds a tenantId property to domain classes annotated MultiTenant.
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class MultiTenantAST implements ASTTransformation {
    private static final ConfigObject CO = getConfig();
    
    public final static String TENANT_ID_FIELD_NAME = "tenantId";

    // TODO: ConstantExpression.NULL would be better, but that leads to all sorts of crazy problems
    // with default GORM constraints. It looks like it will become easier to hook into this in Grails 1.4:
    // https://github.com/grails/grails-core/blob/master/grails-core/src/main/groovy/org/codehaus/groovy/grails/validation/ConstraintsEvaluatorFactoryBean.java
    public final static Integer NO_TENANT_VALUE = Integer.MIN_VALUE;

    @Override
    public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        ClassNode annotatedClassNode = (ClassNode) astNodes[1];
        String tname = tenantFieldName(); //FIXME get this from config
        addTenantPropertyIfNecessary(annotatedClassNode,tname);
        implementInterface(annotatedClassNode, MultiTenantDomainClass.class,tname);
    }
    
    public String tenantFieldName(){
        String tname = CO.toProperties().getProperty("multiTenant.tenantField.name"); 
        //System.out.println("multiTenant.tenantField.name - " + tname); 
        return tname!=null ? tname : "tenantId";
    }

    private void addTenantPropertyIfNecessary(ClassNode classNode,String tenantField) {
        if (!hasTenantIdProperty(classNode)) {
            addTenantProperty(classNode,tenantField);
        }
    }

    private boolean hasTenantIdProperty(ClassNode node) {
        return GrailsASTUtils.hasOrInheritsProperty(node, "tenantId");
    }

    private void addTenantProperty(ClassNode node,String tenantField) {
        ClassNode integerType = new ClassNode(Integer.class);
        ConstantExpression defaultValue = new ConstantExpression(NO_TENANT_VALUE);
        Statement getterBlock = null;
        Statement setterBlock = null;
        node.addProperty(tenantField, Modifier.PUBLIC,
                integerType, defaultValue, getterBlock, setterBlock);
    }

    private void implementInterface(ClassNode node, Class<?> interf,String tenantField) {
        ClassNode interfaceClassNode = new ClassNode(interf);
        if (!node.implementsInterface(interfaceClassNode)) {
            node.addInterface(interfaceClassNode);
        }
        if(tenantField != "tenantId"){
            implementInterfaceSetGet( node, tenantField);
        }
    }
    
    private void implementInterfaceSetGet(ClassNode node, String tenantField) {
		Parameter[] sparams = { (new Parameter(ClassHelper.Integer_TYPE, "val")) };
		BlockStatement setStmt = (BlockStatement) new AstBuilder().buildFromString(tenantField + " = val; return;").get(0);
        node.addMethod("setTenantId", Modifier.PUBLIC, ClassHelper.VOID_TYPE, sparams, null, setStmt);
        BlockStatement getStmt = (BlockStatement) new AstBuilder().buildFromString("return " + tenantField + ";").get(0);
        node.addMethod("getTenantId", Modifier.PUBLIC, ClassHelper.Integer_TYPE, Parameter.EMPTY_ARRAY, null,getStmt );
        addTransientsForTenantId(node);
    }
    
    public void addTransientsForTenantId(ClassNode node) {
        FieldNode pn = node.getField("transients");
        if(pn == null){
            node.addField("transients", ACC_PUBLIC | ACC_STATIC, new ClassNode(java.lang.Object.class), null);
            pn = node.getField("transients");
            //System.out.println("new pn - " + pn);
        }
        ListExpression le = (ListExpression)pn.getInitialExpression();
        if(le ==null ){
            le = new ListExpression(); 
            pn.setInitialValueExpression(le); 
        }
        le.addExpression(new ConstantExpression("tenantId") );
        //System.out.println("ListExpression after - " + le);                                   
	}
	
	static public ConfigObject getConfig() {
	    File aFile = new File("./grails-app/conf/Config.groovy");
		//...checks on aFile are elided
		StringBuilder contents = new StringBuilder();

		try {
			//use buffering, reading one line at a time
			//FileReader always assumes default encoding is OK!
			BufferedReader input =  new BufferedReader(new FileReader(aFile));
			try {
				String line = null; 
				while (( line = input.readLine()) != null){
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			}
			finally {
				input.close();
			}
		}
		catch (IOException ex){
			ex.printStackTrace();
		}
        return new ConfigSlurper().parse(contents.toString());
	}
}