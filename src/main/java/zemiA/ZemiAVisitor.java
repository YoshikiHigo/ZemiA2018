package zemiA;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;

public class ZemiAVisitor extends ASTVisitor {

	private int privatefields;

	@Override
	public boolean visit(SimpleName node) {
		//System.out.println(node.getIdentifier());
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		System.out.println(node.toString());
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

}
