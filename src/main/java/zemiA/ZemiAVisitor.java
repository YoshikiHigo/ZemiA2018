package zemiA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;

public class ZemiAVisitor extends ASTVisitor {

	private int privatefields;
  CompilationUnit unit;
  private String methodname;
  private Map<String,Boolean> fieldmap= new HashMap<String,Boolean>();
  private ArrayList<String> methodlist = new ArrayList<String>();
  private Map<String,Boolean> accessormethodmap= new HashMap<String,Boolean>();
	public ZemiAVisitor(CompilationUnit compilationUnit) {
		this.unit = compilationUnit;
	}

	@Override
	public boolean visit(SimpleName node) {
		//System.out.println(node.getIdentifier());
		return super.visit(node);
	}

  @Override
  public boolean visit(MethodDeclaration node) {
    System.out.print(node.getName() + " method LOC = ");
    System.out.println(unit.getLineNumber(node.getStartPosition() + node.getLength() -1)+1-unit.getLineNumber(node.getStartPosition()));
    methodname = node.getName().toString();
	accessormethodmap.put(methodname, Boolean.FALSE);
	methodlist.add(methodname);
	System.out.println("*"+node.getName()+"*");
	System.out.println(node.resolveBinding().getDeclaringClass());

    return super.visit(node);
  }

	@Override
	public boolean visit(PrimitiveType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	//NOPA Number of public attributes /Entity: Class
	//public修飾子のついたfield変数の数
	@Override
	public boolean visit(FieldDeclaration node) {
		System.out.print(node.toString());
		if(Modifier.isPublic(node.getModifiers())) {
			this.privatefields+=1;
			System.out.println("isPublic");
		}
		System.out.println(this.privatefields);
		return super.visit(node);
	}

	@Override
	public boolean visit(ReturnStatement node) {
		System.out.println(node.getExpression());
		if(node.getExpression().toString().startsWith("this")) {
			System.out.println("getter method");
			accessormethodmap.replace(methodname, Boolean.TRUE);
		}else if(fieldmap.get(node.getExpression().toString())==null) {
			System.out.println("normal method");
		}else if(fieldmap.get(node.getExpression().toString())) {
			System.out.println("*"+"getter method"+"*");
			accessormethodmap.replace(methodname, Boolean.TRUE);
		}
		return super.visit(node);
	}


}
