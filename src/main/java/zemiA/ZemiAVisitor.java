package zemiA;

import java.util.Hashtable;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ZemiAVisitor extends ASTVisitor {

	private int cint = 0;	//the number of distinct method invocation
	private Hashtable<IMethodBinding,ITypeBinding> table = new Hashtable<IMethodBinding,ITypeBinding>();
	private double cdisp = 0;	//the number of class which define called method devided by CINT
	private int nesting = 0;
	private int maxNesting = 0;

	@Override
	public boolean visit(SimpleName node) {
		//System.out.println(node.getIdentifier());
		return super.visit(node);
	}

	@Override
	public void endVisit(TypeDeclaration node) {
		//System.out.println(node.getName());
		super.endVisit(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		//System.out.println(node.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodInvocation node) {
		//System.out.println(node.toString());
		/* invoked method is distinct method -> cint++ */

		IMethodBinding bind = node.resolveMethodBinding();
		//System.out.println(node.resolveMethodBinding().getName());
		System.out.println(bind.getDeclaringClass().getName());

		if(isDistinctMethod(node)) {
			cint++;
			table.put(bind,bind.getDeclaringClass());
		}
		return super.visit(node);
	}

	private boolean isDistinctMethod(MethodInvocation node) {

		return true;  //Developping
	}
	@Override
	public boolean visit(Block node) {
		nesting++;
		if(maxNesting < nesting) {
			maxNesting = nesting;
		}
		return super.visit(node);
	}

	@Override
	public void endVisit(Block node) {
		nesting--;
		super.endVisit(node);
	}

	public int getCINT() {
		return cint;
	}

	public double getCDISP() {
		cdisp = table.size() / cint;
		return cdisp;
	}

	public int getMaxNesting() {
		return maxNesting - 1;  //maxNesting include method difinition block
	}

	public boolean isIntensiveCoupling() {
		boolean intensiveCoupling1 = getCINT()>7 && getCDISP()<1/2;
		boolean intensiveCoupling2 = getCINT()>3 && getCDISP()<1/4;
		boolean deepNesting = getMaxNesting()<3;
		return (intensiveCoupling1 || intensiveCoupling2) && deepNesting;
	}

	public boolean isDispersedCoupling() {
		boolean dispersedCoupling = getCINT()>7 && getCDISP()>=1/2;
		boolean deepNesting = getMaxNesting()<3;
		return dispersedCoupling && deepNesting;
	}
}
