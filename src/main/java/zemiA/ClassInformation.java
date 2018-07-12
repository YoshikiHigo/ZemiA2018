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
	private int classWOC = 0;
	private int classLOC = 0;

	private int noam=0;//the Number of Acceccor Methods
	private int nopa=0;//the Number Of Public Attributes
	private int classatfddirect=0;
	private int classatfdviamethod=0;
	private List<IMethodBinding> invokingmethod = new ArrayList<IMethodBinding>();
	private List<String> declaringfieldlist = new ArrayList<String>();

//	public int dummy;//NOPA test

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
		HashMap<String,ITypeBinding> invokedClasses = new HashMap<String,ITypeBinding>();
		for(IMethodBinding invokedMethod: invokedMethods) {
			ITypeBinding invokedClassBind = invokedMethod.getDeclaringClass();
			String invokedClassName = invokedClassBind.getName().toString();
			invokedClasses.put(invokedClassName,invokedClassBind);
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

	public void setClassWOC(int woc) {
		classWOC = woc;
	}

	public int getClassWOC() {
		return classWOC;
	}

	public void setClassLOC(int loc) {
		classLOC = loc;
	}

	public int getClassLOC() {
		return classLOC;
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
		System.out.println("WOC: "+ getClassWOC());
		System.out.println("LOC: "+ getClassLOC());
		System.out.println("NOAM: "+ getNOAM());
		System.out.println("NOPA: "+ getNOPA());
		System.out.println("ATFD: "+ getClassATFD());
		System.out.println("intensive coupling: " + isIntensiveCoupling());
		System.out.println("dispersed coupling: " + isDispersedCoupling());

		// to refactoring
//		System.out.println("invocate method list");
//		for(IMethodBinding invokedMethod: invokedMethods) {
//			String invokedClassName = invokedMethod.getDeclaringClass().getName().toString();
//			System.out.println(invokedClassName + "." +invokedMethod.getName().toString());
//		}

		System.out.println("");
	}

	public void setNOAM(int n) {
		noam = n;
	}

	public int getNOAM() {
		return noam;
	}

	public void setNOPA(int argnopa) {
		this.nopa = argnopa;
	}

	public int getNOPA() {
		return this.nopa;
	}

	public void setClassATFD(int argclassatfd) {
		this.classatfddirect = argclassatfd;
	}

	public int getClassATFD() {
		return this.classatfddirect+this.classatfdviamethod;
	}

	public void setInvokingMethods(IMethodBinding mb) {
		this.invokingmethod.add(mb);
	}

	public List<IMethodBinding> getInvokingMethods() {
		return this.invokingmethod;
	}

	public void setDeclaringFieldList(String s) {
		this.declaringfieldlist.add(s);
	}

	public List<String> getDeclaringFieldList() {
		return this.declaringfieldlist;
	}

	public boolean isDefined(IMethodBinding mb) {
		return this.classMethods.contains(mb);
	}

	public boolean isDefined(String fieldname) {
		return this.declaringfieldlist.contains(fieldname);
	}
}
