package zemiA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class MethodInformation{
	private String methodName;
	private IMethodBinding methodBind;
	private int cint = 0;	//the number of distinct method invocation
	private double cdisp = 0;	//the number of class which define called method devided by CINT
	private int maxNesting = 0;
	private List<IMethodBinding> invokedMethods = new ArrayList<IMethodBinding>();
	private int cm = 0;
	private int cc = 0;
	private List<IMethodBinding> invokingMethods = new ArrayList<IMethodBinding>();

	public MethodInformation(IMethodBinding declaratedMethodBind) {
		methodBind = declaratedMethodBind;
		methodName = methodBind.getName().toString();
	}

	// Setter
	public void invocated(IMethodBinding invokingMethodBind) {
		invokingMethods.add(invokingMethodBind);
		cm++;
	}

	// Setter
	public void invocate(IMethodBinding invokedMethodBind) {
		cint++;
		invokedMethods.add(invokedMethodBind);
	}

	public int getMaxNesting() {
		return maxNesting;
	}

	public void setMaxNesting(int MAXNESTING) {
		maxNesting = MAXNESTING;
	}


	public int getCINT() {
		return cint;
	}

	public double getCDISP() {
		HashMap<ITypeBinding,Integer> invokedClasses = new HashMap<ITypeBinding,Integer>();
		for(IMethodBinding invokedMethod: invokedMethods) {
			ITypeBinding invokedClassBind = invokedMethod.getDeclaringClass();
			Integer i = invokedClasses.get(invokedClassBind);
			i = (i==null)? 0 : i;  //if i==null: first invocation
			invokedClasses.put(invokedClassBind,++i);
		}
		if(cint != 0) {
			cdisp = (double)invokedClasses.size() / cint;
		}
		return cdisp;
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

	// CM: times of invocated by distinct method
	public int getCM() {
		return cm;
	}

	// CC: number of class which define CM method
	public int getCC() {
		HashMap<ITypeBinding,Integer> invokingClassesList = new HashMap<ITypeBinding,Integer>();
		for(IMethodBinding invokingMethodBind: invokingMethods) {
			Integer i = invokingClassesList.get(invokingMethodBind.getDeclaringClass());
			i = (i==null)? 0 : i;  //if i==null: first invocation
			invokingClassesList.put(invokingMethodBind.getDeclaringClass(),++i);
		}
		cc = invokingClassesList.size();
		return cc;
	}

	public boolean isShotgunSurgery() {
		// CM > ShortMemoryCapacity 7
		// CC > MANY 4
		return getCM()>7 && getCC()>4;
	}

	public IMethodBinding getMethodBinding() {
		return methodBind;
	}

	public void printMethodInfomation(){
		ITypeBinding[] argumentsBind = methodBind.getTypeArguments();
		List<String> argumentsString = new ArrayList<String>();
		for(ITypeBinding argument: argumentsBind) {
			argumentsString.add(argument.getName().toString());
		}
		String arguments = "(" + String.join(", ", argumentsString) + ")";

		// TODO 引数のリストが常に空(APIになんか書いてるけどジェネリックメソッドってなんですか)
		System.out.println("methodName: " + methodName + arguments);
		System.out.println("declarated class: " + methodBind.getDeclaringClass().getBinaryName().toString());
		System.out.println("CINT: " + getCINT());
		System.out.println("CDISP: " + getCDISP());
		System.out.println("MAXNESTING: " + getMaxNesting());
		System.out.println("CM: " + getCM());
		System.out.println("CC: " + getCC());
		System.out.println("intensive coupling: " + isIntensiveCoupling());
		System.out.println("dispersed coupling: " + isDispersedCoupling());
		System.out.println("shotgun surgery: " + isShotgunSurgery());
		System.out.println("----------------------------------------------------");
	}

	public static MethodInformation getMethodInformation(IMethodBinding methodBind, List<MethodInformation> methods) {
		for(MethodInformation methodInformation: methods) {
			if(methodInformation.getMethodBinding().equals(methodBind)) {
				return methodInformation;
			}
		}
		// if the method is not project method
		return null;
	}
}
