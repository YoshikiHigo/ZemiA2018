package zemiA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class ClassInformation {
	private String className;
	private ITypeBinding classBind;
	private List<IMethodBinding> invokedMethods = new ArrayList<IMethodBinding>();
	private List<IMethodBinding> classMethods;
	private int cint = 0;	//the number of distinct method invocation
	private double cdisp = 0;	//the number of class which define called method devided by CINT
	private int maxNesting = 0;
	
	private int nom = 0;
	private int wmc = 0;//=WMC, complexity
	private double amw = -1;
	private int nprotm = 0;
	private double bovr = -1;
	private int nas = -1;
	private double pnas = -1;
	private double bur = -1;
	private String parentName = null;
	private ITypeBinding parentBinding = null;
	private ClassInformation parentClass = null;
	private List<ClassInformation> childClass = new ArrayList<ClassInformation>();
	private List<MethodDeclaration> pmlist;//public method list
	private List<MethodDeclaration> ismlist;//public or protected method list
	private List<FieldDeclaration> alist;//fieldlist
	private List<FieldDeclaration> isalist;//public or protected field list
	private List<IVariableBinding> usedSuperFields;
	private List<IMethodBinding> usedSuperMethods;
	
	

	public static final int FOR_DISPLAY = 0;
	public static final int FOR_REFACTORING = 1;

	public ClassInformation(ITypeBinding typeBinding) {
		classBind = typeBinding;
		className = classBind.getName().toString();
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

	public void printClassInformation(int mode) {
		if(mode == FOR_DISPLAY || mode == FOR_REFACTORING) {
			System.out.println("Class name: "+ className);
			if(parentName != null)System.out.println("Parent Class name: "+parentName);
			System.out.println("CINT: " + getCINT());
			System.out.println("CDISP: " + getCDISP());
			System.out.println("MAXNESTING: " + getMaxNesting());
			System.out.println("intensive coupling: " + isIntensiveCoupling());
			System.out.println("dispersed coupling: " + isDispersedCoupling());
			System.out.println("NOM: "+nom);
			System.out.println("WMC: "+wmc);
			System.out.println("AMW: "+amw);
			System.out.println("NProtM: "+nprotm);
			System.out.println("BOvR: "+bovr);
			System.out.println("NAS: "+nas);
			System.out.println("PNAS: "+pnas);
			System.out.println("BUR: "+bur);
			System.out.println("Refused Parent Bequest: "+this.isRPB());
			System.out.println("Tradition Breaker: "+this.isTB());
		}

		if(mode == FOR_REFACTORING) {
			// for refactoring
			System.out.println("----- " + className + "'s invocate method list -----");
			for(IMethodBinding invokedMethod: invokedMethods) {
				String invokedClassName = invokedMethod.getDeclaringClass().getName().toString();
				System.out.println(invokedClassName + "." +invokedMethod.getName().toString());
			}
		}

		System.out.println();
	}
	

	public static ClassInformation getClassInformation(ITypeBinding classBind, List<ClassInformation> classes) {
		for(ClassInformation classInformation: classes) {
			if(classInformation.getClassBinding().equals(classBind)) {
				return classInformation;
			}
		}
		return null;
	}
	public ITypeBinding getParentBindig() {
		return parentBinding;
	}
	
	
	public double getBOvR() {
		return bovr;
	}
	public void setBOvR(double BOvR) {
		if(nom != 0) bovr = BOvR/nom;
	}
	public int getNAS() {
		return nas;
	}
	public void setNAS(int NAS) {
		nas = pmlist.size() - NAS;
	}
	public double getPNAS() {
		return pnas;
	}
	public void setPNAS() {
		if(pmlist.size() != 0)pnas = (double)nas/pmlist.size();
	}
	public double getBUR() {
		return bur;
	}
	public void setBUR(int BUR) {
		if(BUR != 0)bur = (double)(usedSuperFields.size()+usedSuperMethods.size())/BUR;
	}
	public ClassInformation getParent() {
		return parentClass;
	}
	public void setParentClass(ClassInformation parent) {
		this.parentClass = parent;
	}
	public void setParent(ITypeBinding parent) {
		this.parentName = parent.getBinaryName();
		this.parentBinding = parent;
	}
	public List<MethodDeclaration> getPmlist() {
		return pmlist;
	}
	public List<MethodDeclaration> getIsmlist() {
		return ismlist;
	}
	public List<FieldDeclaration> getAlist() {
		return alist;
	}
	public List<FieldDeclaration> getIsalist() {
		return isalist;
	}
	
	public boolean isRPB() {
		return ((nprotm > 3 && bur < (double)1/3) || bovr < (double)1/3)
				&& ((amw > 2.0 || wmc > 14) && nom > 7);
	}
	
	public boolean isTB() {
		return (nas >= 7 && pnas < (double)2/3)
		&& ((amw > 2.0 || wmc >= 47) && nom >= 10)
		&& (amw > 2.0 && nom > 5 && wmc >= 14);
	}
	public void addChildClass(ClassInformation child) {
		childClass.add(child);
	}
	
	//TODO NOM,WMC,NProtM,LOCなどもまとめて入れる
	public void setClassInformation(List<IMethodBinding> allDeclaratingMethods, int MAXNESTING,
			int NOM, int WMC, int NprotM, List<MethodDeclaration> PmList,
			List<MethodDeclaration> ISMList, List<FieldDeclaration> AList, List<FieldDeclaration> ISAList,
			List<IVariableBinding> UsedSuperFields, List<IMethodBinding> UsedSuperMethods) {
		classMethods = allDeclaratingMethods;
		maxNesting = MAXNESTING;
		nom = NOM;
		wmc = WMC;
		if(nom != 0) amw = (double)wmc/nom;
		nprotm = NprotM;
		pmlist = PmList;
		ismlist = ISMList;
		alist = AList;
		isalist = ISAList;
		usedSuperFields = UsedSuperFields;
		usedSuperMethods = UsedSuperMethods;
	}
	
	public void setInheritanceInformation(double ovnum, int povnum) {
		if(nom != 0) bovr = ovnum/nom;
		nas = pmlist.size() - povnum;
		if(pmlist.size() != 0)pnas = (double)nas/pmlist.size();
		int pismnum = parentClass.getIsalist().size() + parentClass.getIsmlist().size();
		if(pismnum != 0)bur = (double)(usedSuperFields.size()+usedSuperMethods.size())/pismnum;
	}

}
