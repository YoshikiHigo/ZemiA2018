package zemiA;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
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
	private Hashtable<String,ITypeBinding> classTable = new Hashtable<String,ITypeBinding>();
	private List<IMethodBinding> allMethods;

	public ZemiAVisitor() {
		super();

		//allMethods = a;
	}

	@Override
	public void endVisit(CompilationUnit node) {
		// TODO 自動生成されたメソッド・スタブ
		allMethods.addAll(methods);
		// printClassImformations
		System.out.println("Class name: "+ getClassName());
		System.out.println("CINT: " + getCINT());
		System.out.println("CDISP: " + getCDISP());
//		Enumeration<ITypeBinding> elements = classTable.elements();
//		while(elements.hasMoreElements()) {
//			System.out.println(elements.nextElement().getName().toString());
//		}
		System.out.println("MAXNESTING: " + getMaxNesting());
		System.out.println("intensive coupling: " + isIntensiveCoupling());
		System.out.println("dispersed coupling: " + isDispersedCoupling());
//		for(IMethodBinding m: allMethods){
//			System.out.println(m.getName().toString());
//		}
		System.out.println("");

		super.endVisit(node);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		className = node.getName().toString();
		for(MethodDeclaration mb: node.getMethods()) {
			methods.add(mb.resolveBinding());
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodInvocation node) {
		//System.out.println(node.toString());
		IMethodBinding methodBind = node.resolveMethodBinding();
		ITypeBinding classBind = methodBind.getDeclaringClass();
		String className = classBind.getName().toString();
		if(isDistinctMethod(methodBind) && isProjectMethod(methodBind)) {
			cint++;
			classTable.put(className,classBind);

			/***************************************/
			boolean a;
			for(IMethodBinding b: allMethods) {
				a = node.resolveMethodBinding().equals(b);
				if(a)System.out.println(node.getName().toString()+a);
			}
			/**************************************/
		}
		return super.visit(node);
	}

	private boolean isDistinctMethod(IMethodBinding bind) {
		// if distinct method contains() returns false
		return !methods.contains(bind);
	}

	/*always return true: Developping...*/
	private boolean isProjectMethod(IMethodBinding bind) {
		return allMethods.contains(bind);
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
			cdisp = (double)classTable.size() / cint;
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

	public List<MethodInformation> getShotgunSurgeryMethodList(){
		List<MethodInformation> shotgunSergeryMethods = new ArrayList<MethodInformation>();
//		MethodInformation ms;
//		Enumeration<MethodInformation> elements = invocatedMethods.elements();
//		while(elements.hasMoreElements()) {
//			ms = elements.nextElement();
//			//System.out.println(mb.getMethodBinding().getName().toString() + mb.getCC());
//			if(ms.getCC()>7 && ms.getCC()>5) {
//				shotgunSergeryMethods.add(ms);
//			}
//		}
		return shotgunSergeryMethods;
	}
}
