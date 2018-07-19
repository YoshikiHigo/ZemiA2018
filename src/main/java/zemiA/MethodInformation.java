package zemiA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class MethodInformation{

	private String methodName;  //include arguments
	private IMethodBinding methodBind;
	private int cint = 0;	//the number of distinct method invocation
	private double cdisp = 0;	//the number of class which define called method devided by CINT
	private int maxNesting = 0;
	private List<IMethodBinding> invokedMethods = new ArrayList<IMethodBinding>();
	private int cm = 0;
	private int cc = 0;
	private List<IMethodBinding> invokingMethods = new ArrayList<IMethodBinding>();

	private boolean accessor = false;
	private List<String> parameters = new ArrayList<String>();
	private int methodatfddirect=0;
	private int methodatfdvaimethod=0;
	private int methodatfd=0;
	private int noav=0;//the Number of accessed variables
	private int fdp=0;//foreign data provider
	private double laa=-1;
	private List<IMethodBinding> invokingmethod = new ArrayList<IMethodBinding>();
	private List<String> accessedfeildlist = new ArrayList<String>();
	private List<String> accessednormalvariables = new ArrayList<String>();
	private List<ITypeBinding> providerclasses = new ArrayList<ITypeBinding>();
	private ITypeBinding declaringclass;
	private int fromownclass=0;
	private int fromforeignclass=0;
	private int methodLOC = 0;
	private int wmc = 0;

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

	public String getName() {
		ITypeBinding[] argumentsBind = methodBind.getParameterTypes();
//		List<String> argumentsString = new ArrayList<String>();
//		for(ITypeBinding argument: argumentsBind) {
//			argumentsString.add(argument.getName().toString());
//		}
//		String arguments = "(" + String.join(", ", argumentsString) + ")";
		String arguments = Arrays.asList(argumentsBind).stream()
				.map(argument -> argument.getName().toString())
				.collect(Collectors.joining(","));
		return methodName + "(" + arguments + ")";
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
	
	public boolean isBrainMethod() {
		if((methodLOC>65)&&(wmc>=31)&&(maxNesting>=5)&&(noav > 7))return true;
		else return false;
	}
	
	public boolean isFeatureEnvy() {
		if((methodatfd>5)&&(laa<1/3)&&(fdp<=2)) return true;
		else return false;
	}

	public void setMethodLOC(int loc) {
		methodLOC = loc;
	}

	public int getMethodLOC() {
		return methodLOC;
	}


	public void setAccessor(boolean b) {
		this.accessor = b;
	}

	public boolean isAccessor() {
		return this.accessor;
	}

	public void setParameter(String s) {
		this.parameters.add(s);
	}

	public List<String> getParameter(){
		return this.parameters;
	}

	public void setMethodATFDDirect(int argmethodatfd) {
		this.methodatfddirect=argmethodatfd;
	}

	public int getMethodATFDDirect() {
		return this.methodatfddirect;
	}

	public void setMethodATFDViaMethod(int argmethodatfd) {
		this.methodatfdvaimethod=argmethodatfd;
	}

	public int getMethodATFDViaMethod() {
		return this.methodatfdvaimethod;
	}

	public void setInvokingMethods(IMethodBinding mb) {
		this.invokingmethod.add(mb);
	}

	public List<IMethodBinding> getInvokingMethods() {
		return this.invokingmethod;
	}

	public void setAccessedFields(String s) {
		this.accessedfeildlist.add(s);
	}

	public List<String> getAccessedFields() {
		return this.accessedfeildlist;
	}

	public void setNormalVariableList(String s) {
		this.accessednormalvariables.add(s);
	}

	public List<String> getNormalVariableList() {
		return this.accessednormalvariables;
	}

	public void setNOAV() {
		this.noav=this.accessedfeildlist.size()+this.accessednormalvariables.size()+this.parameters.size();
	}

	public int getNOAV() {
		return this.noav;
	}

	public void setmethodATFD() {
		this.methodatfd=this.methodatfddirect+this.methodatfdvaimethod;
	}

	public int getmethodATFD() {
		return this.methodatfd;
	}

	public boolean isAccessed(String s) {
		return this.accessedfeildlist.contains(s);
	}

	public boolean isInvoked(IMethodBinding mb) {
		return this.invokingmethod.contains(mb);
	}

	public void setProviderClasses(ITypeBinding tp) {
		if(this.declaringclass.equals(tp)) {
			this.fromownclass+=1;
		}else if(this.providerclasses.contains(tp)) {
			this.fromforeignclass+=1;
		}else {
			this.providerclasses.add(tp);
			this.fromforeignclass+=1;
		}
	}

	public List<ITypeBinding> getProviderClasses(){
		return this.providerclasses;
	}

	public void setFDP() {
		this.fdp = this.providerclasses.size();
	}

	public int getFDP() {
		return this.fdp;
	}

	public void setDeclaringClass(ITypeBinding tb) {
		this.declaringclass=tb;
	}

	public ITypeBinding getDeclaringClass() {
		return this.declaringclass;
	}

	public void setLAA() {
		if(this.fromownclass+this.fromforeignclass != 0) this.laa=(double)this.fromownclass/(this.fromownclass+this.fromforeignclass);
	}

	public double getLAA() {
		return this.laa;
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
	
	public void setWMC(int WMC) {
		wmc = WMC;
	}
	
	public int getWMC() {
		return wmc;
	}

	public void printMethodInfomation(){
		System.out.println("methodName: " + getName());
		System.out.println("declarated class: " + methodBind.getDeclaringClass().getBinaryName().toString());
		System.out.println("ATFD: " + methodatfd);
		System.out.println("CC: " + cc);
		System.out.println("CDISP: " + cdisp);
		System.out.println("CINT: " + cint);
		System.out.println("CM: " + cm);
		System.out.println("FDP: " + fdp);
		if(laa != -1) System.out.println("LAA: " + laa);
		else System.out.println("LAA: no access");
		System.out.println("LOC: " + methodLOC);
		System.out.println("MAXNESTING: " + maxNesting);
		System.out.println("NOAV: " + noav);
		System.out.println("WMC: " + wmc);
		System.out.println("Brain Method: " + isBrainMethod());
		System.out.println("Feature Envy: " + isFeatureEnvy());
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

	// TODO メソッドごとの不調和があったらここに追記してください
	public int numOfDisharmony() {
		int count = 0;

		if(isIntensiveCoupling()) count++;
		if(isDispersedCoupling()) count++;
		if(isShotgunSurgery()) count++;

		return count;
	}
}
