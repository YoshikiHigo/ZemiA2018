package zemiA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ZemiAVisitor extends ASTVisitor {

	private String className;
	private int cint = 0;	//the number of distinct method invocation
	private double cdisp = 0;	//the number of class which define called method devided by CINT
	private int nesting = 0;
	private int maxNesting = 0;
	private List<IMethodBinding> methods = new ArrayList<IMethodBinding>();
	private Hashtable<String,ITypeBinding> table = new Hashtable<String,ITypeBinding>();
	private List<IBinding> imports = new LinkedList<IBinding>();

	@Override
	public boolean visit(ImportDeclaration node) {
		//System.out.println(node.getName().toString());
		imports.add(node.resolveBinding());
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		className = node.getName().toString();
		for(MethodDeclaration md : Arrays.asList(node.getMethods())) {
			methods.add(md.resolveBinding());
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodInvocation node) {
		//System.out.println(node.toString());
		IMethodBinding bind = node.resolveMethodBinding();
		if(isDistinctMethod(bind) && isProjectMethod(bind)) {
			cint++;
			table.put(node.getName().toString(),bind.getDeclaringClass());
		}
		return super.visit(node);
	}

	private boolean isDistinctMethod(IMethodBinding bind) {
		// if distinct method contains() returns false
		return !methods.contains(bind);
	}

	/*always return true: Developping...*/
	private boolean isProjectMethod(IMethodBinding bind) {
		boolean importFlag = false;
		IBinding binding = bind;
		for(IBinding b: imports) {
			importFlag = binding.isEqualTo(b);
		}
		return !importFlag;
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

	public String getClassName() {
		return className;
	}

	public int getCINT() {
		return cint;
	}

	public double getCDISP() {
		if(cint != 0) {
			cdisp = (double)table.size() / cint;
		}
		return cdisp;
	}

	public int getMaxNesting() {
		//maxNesting include method difinition block
		return maxNesting - 1;
	}

	public boolean isIntensiveCoupling() {
		boolean intensiveCoupling1 = getCINT()>7 && getCDISP()<1/2;
		boolean intensiveCoupling2 = getCINT()>3 && getCDISP()<1/4;
		boolean deepNesting = getMaxNesting()>3;
		return (intensiveCoupling1 || intensiveCoupling2) && deepNesting;
	}

	public boolean isDispersedCoupling() {
		boolean dispersedCoupling = getCINT()>7 && getCDISP()>=1/2;
		boolean deepNesting = getMaxNesting()>3;
		return dispersedCoupling && deepNesting;
	}
}
