package zemiA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class ClassInformation {
	ITypeBinding classBind;
	private List<IMethodBinding> invokedMethods = new ArrayList<IMethodBinding>();
	private List<IMethodBinding> classMethods;
	private int cint = 0;	//the number of distinct method invocation
	private double cdisp = 0;	//the number of class which define called method devided by CINT
	private int maxNesting = 0;

	public ClassInformation(ITypeBinding typeBinding) {
		classBind = typeBinding;
	}

	// setter
	public void invocate(IMethodBinding invokedMethodBind) {
		cint++;
		invokedMethods.add(invokedMethodBind);
	}

	public int getCINT() {
		return cint;
	}

	public double getCDISP() {
		HashMap<ITypeBinding,Integer> invokedClasses = new HashMap<ITypeBinding,Integer>();
		for(IMethodBinding invokedMethod: invokedMethods) {
			ITypeBinding invokedClassBind = invokedMethod.getDeclaringClass();
			Integer i = invokedClasses.get(invokedClassBind);
			i = i==null? 0 : i;  //if i==null: first invocation
			invokedClasses.put(invokedClassBind,++i);
		}
		if(cint != 0) {
			cdisp = (double)invokedClasses.size() / cint;
		}
		return cdisp;
	}

	public ITypeBinding getClassBinding() {
		return classBind;
	}

	public void setMethodList(List<IMethodBinding> methods) {
		classMethods = methods;
	}

	public List<IMethodBinding> getMethodsList(){
		return classMethods;
	}


	public boolean setMaxNesting(int nesting) {
		if(nesting<0) {
			return false;
		}else {
			maxNesting = nesting;
			return true;
		}
	}

	public int getMaxNesting() {
		//maxNesting include method definition block
		return maxNesting;
	}

	public boolean isIntensiveCoupling() {
		// CINT: ShortMemoryCap 7, CDISP: HALF
		boolean intensiveCoupling1 = getCINT()>7 && getCDISP()<(double)1/2;
		// CINT: FEW 4, CDISP: A QUARTER
		boolean intensiveCoupling2 = getCINT()>4 && getCDISP()<(double)1/4;
		boolean deepNesting = getMaxNesting()>1;  // MAXNESTING: SHALLOW 1
		return (intensiveCoupling1 || intensiveCoupling2) && deepNesting;
	}

	public boolean isDispersedCoupling() {
		// CINT: ShortMemoryCap 7, CDISP: HALF
		boolean dispersedCoupling = getCINT()>7 && getCDISP()>=(double)1/2;
		boolean deepNesting = getMaxNesting()>1;  // MAXNESTING: SHALLOW 1
		return dispersedCoupling && deepNesting;
	}

	public void printClassInformation() {
		System.out.println("Class name: "+ classBind.getName().toString());
		System.out.println("CINT: " + getCINT());
		System.out.println("CDISP: " + getCDISP());
		System.out.println("MAXNESTING: " + getMaxNesting());
		System.out.println("intensive coupling: " + isIntensiveCoupling());
		System.out.println("dispersed coupling: " + isDispersedCoupling());

		// for refactoring
		System.out.println("invocate method list");
		for(IMethodBinding invokedMethod: invokedMethods) {
			String invokedClassName = invokedMethod.getDeclaringClass().getName().toString();
			System.out.println(invokedClassName + "." +invokedMethod.getName().toString());
		}

		System.out.println("");
	}

}
