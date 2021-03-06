package zemiA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class ClassInformation {
	private String className;
	private ITypeBinding classBind;
	private List<IMethodBinding> classMethods;
	private List<MethodInformation> classMethodsInformation = new ArrayList<MethodInformation>();
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

  private double classWOC = -1;
	private int classLOC = 0;

	private int noam=0;//the Number of Acceccor Methods
	private int nopa=0;//the Number Of Public Attributes
	private int classatfd=0;
	private double tcc=-1;
	private List<IMethodBinding> invokingmethod = new ArrayList<IMethodBinding>();
	private List<String> declaringfieldlist = new ArrayList<String>();

//	public int dummy;//NOPA test


	public ClassInformation(ITypeBinding typeBinding) {
		classBind = typeBinding;
		className = classBind.getName().toString();

	}

	public ITypeBinding getClassBinding() {
		return classBind;
	}

	//legacy
//	public void setMethodList(List<IMethodBinding> methods) {
//		classMethods = methods;
//	}

	public List<IMethodBinding> getMethodsList(){
		return classMethods;
	}


	//legacy
//	public boolean setMaxNesting(int nesting) {
//		if(nesting<0) {
//			return false;
//		}else {
//			maxNesting = nesting;
//			return true;
//		}
//	}

	public int getMaxNesting() {
		//maxNesting include method definition block
		return maxNesting;
	}

	public void setClassWOC(double woc) {
		classWOC = woc;
	}

	public double getClassWOC() {
		return classWOC;
	}

	public void setClassLOC(int loc) {
		classLOC = loc;
	}

	public int getClassLOC() {
		return classLOC;
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
		this.classatfd = argclassatfd;
	}

	public int getClassATFD() {
		return this.classatfd;
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

	public void setTCC(double argtcc) {
		this.tcc = argtcc;
	}

	public double getTCC() {
		return this.tcc;
	}

	public void setClassMethods(HashMap<IMethodBinding,MethodInformation> allDeclaratedMethods) {
		for(IMethodBinding classMethod: classMethods) {
			classMethodsInformation.add(allDeclaratedMethods.get(classMethod));
		}
	}

	public void printClassInformation() {
		System.out.println("Class name: "+ className);
		if(parentName != null)System.out.println("Parent Class name: "+parentName);
		System.out.println("AMW: "+amw);
		System.out.println("ATFD: "+classatfd);
		if(bovr!=-1)System.out.println("BOvR: "+ bovr);
		else System.out.println("BOvR: parent class is made by third party");
		if(bur!=-1)System.out.println("BUR: "+bur);
		else System.out.println("BUR: parent class is made by third party");
		System.out.println("LOC: "+classLOC);
		if(nas!=-1)System.out.println("NAS: "+nas);
		else System.out.println("NAS: parent class is made by third party");
		System.out.println("NOAM: "+noam);
		System.out.println("NOM: "+nom);
		System.out.println("NOPA: "+nopa);
		System.out.println("NProtM: "+nprotm);
		if(pnas!=-1)System.out.println("PNAS:" +pnas);
		else System.out.println("PNAS: parent class is made by third party");
		if(tcc != -1) System.out.println("TCC: "+tcc);
		else System.out.println("TCC: no Tight Class Cohesion");
		System.out.println("WMC: "+wmc);
		if(classWOC != -1)System.out.println("WOC: "+classWOC);
		else System.out.println("WOC: has no public member");
		System.out.println("God Class: "+this.isGodClass());
		System.out.println("Data Class: "+this.isDataClass());
		System.out.println("BrainClass: "+this.isBrainClass());
		System.out.println("Refused Parent Bequest: "+this.isRPB());
		System.out.println("Tradition Breaker: "+this.isTB());
		System.out.println("Disharmony num:" + this.numOfDisharmony());


		System.out.println("------------this class's method information------------");
		for(MethodInformation classMethod: classMethodsInformation) {
			classMethod.printMethodInfomation();
		}

		System.out.println();
	}

	//legacy
	//	public static ClassInformation getClassInformation(ITypeBinding classBind, List<ClassInformation> classes) {
	//		for(ClassInformation classInformation: classes) {
	//			if(classInformation.getClassBinding().equals(classBind)) {
	//				return classInformation;
	//			}
	//		}
	//		// if the class is not project class
	//		return null;
	//	}
	public ITypeBinding getParentBindig() {
		return parentBinding;
	}
	public void setBUR(int BUR) {
		if(BUR != 0)bur = (double)(usedSuperFields.size()+usedSuperMethods.size())/BUR;
	}

	public ClassInformation getParent() {
		return parentClass;
	}
	public List<ClassInformation> getChild(){
		return childClass;
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

	public double getNProtM() {
		return nprotm;
	}
	public double getBOvR() {
		return bovr;
	}
	public double getNAS() {
		return nas;
	}
	public double getPNAS() {
		return pnas;
	}
	public double getBUR() {
		return bur;
	}
	public double getNOM() {
		return nom;
	}
	public double getWMC() {
		return wmc;
	}
	public double getAMW() {
		return amw;
	}
	public String getName() {
		return className;
	}
	public List<MethodInformation> getMethodInformation(){
		return classMethodsInformation;
	}


	public boolean isRPB() {
		if(parentClass != null) return ((parentClass.getNProtM() > 3 && bur < (double)1/3) || bovr < (double)1/3)
				&& ((amw > 2.0 || wmc > 14) && nom > 7);
		else return false;
	}

	public boolean isTB() {
		if(parentClass != null) return (nas >= 7 && pnas < (double)2/3)
		&& ((amw > 2.0 || wmc >= 47) && nom >= 10)
		&& (parentClass.getAMW() > 2.0 && parentClass.getNOM() > 5 && parentClass.getWMC() >= 14);
		else return false;
	}

	public boolean isGodClass() {
		if((classatfd>5)&&(wmc>=47)&&(tcc<(double)1/3))return true;
		else return false;
	}

	public boolean isDataClass() {
		if((classWOC < (double)1/3)&&(((nopa+noam>5)&&(wmc<31))||((nopa + noam>7)&&(wmc < 47)))) return true;
		else return false;
	}

	public boolean isBrainClass() {
		if((((numOfBrainMethod()==1)&&(classLOC >= 195))||((numOfBrainMethod()>1)&&(classLOC >= 2*195)
				&&(wmc >= 2*47)))&&((wmc>=47)&&(tcc < (double)1/2))) return true;
		else return false;
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

	public List<MethodInformation> getDisharmonyMethods(){
//		List<MethodInformation> disharmonyMethodsList = new ArrayList<MethodInformation>();
//		for(MethodInformation methodInformation :getMethodInformation()) {
//			if(methodInformation.numOfDisharmony()>0) {
//				disharmonyMethodsList.add(methodInformation);
//			}
//		}

		List<MethodInformation> disharmonyMethodsList = classMethodsInformation.stream()
				.filter(methodInformation -> methodInformation.numOfDisharmony() > 0)
				.collect(Collectors.toList());

		return disharmonyMethodsList;
	}

	private int numOfBrainMethod(){
		int num = 0;
		for(MethodInformation mData : classMethodsInformation) {
			if(mData.isBrainMethod()) num++;
		}
		return num;
	}

	public int numOfDisharmony() { //include method disharmony
		int disharmonyCount = 0;
		if(isBrainClass()) disharmonyCount++;
		if(isDataClass()) disharmonyCount++;
		if(isGodClass()) disharmonyCount++;
		if(isRPB()) disharmonyCount++;
		if(isTB()) disharmonyCount++;
		int methodDisharmony = getDisharmonyMethods().stream()
				.collect(Collectors.summingInt(methodInformation -> methodInformation.numOfDisharmony()));

		disharmonyCount += methodDisharmony;

		return disharmonyCount;
	}

}
