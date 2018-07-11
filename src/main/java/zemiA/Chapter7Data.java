package zemiA;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class Chapter7Data {
	private int NOM = 0;
	private int WMC = 0;//=WMC, complexity
	private double AMW = -1;
	private int NProtM = 0;
	private double BOvR = -1;
	private int NAS = -1;
	private double PNAS = -1;
	private double BUR = -1;
	private String name;
	private String pname;
	private Chapter7Data parent;
	private List<Chapter7Data> childs = new ArrayList<Chapter7Data>();
	private List<MethodDeclaration> mlist;//method list
	private List<MethodDeclaration> pmlist;//public method list
	private List<MethodDeclaration> ismlist;//public or protected method list
	private List<FieldDeclaration> alist;//fieldlist
	private List<FieldDeclaration> isalist;//public or protected field list
	private List<IVariableBinding> usedSuperFields;
	private List<IMethodBinding> usedSuperMethods;
	
	public List<IVariableBinding> getUsedSuperFields() {
		return usedSuperFields;
	}
	public void setUsedSuperFields(List<IVariableBinding> usedSuperFields) {
		this.usedSuperFields = usedSuperFields;
	}
	public List<IMethodBinding> getUsedSuperMethods() {
		return usedSuperMethods;
	}
	public void setUsedSuperMethods(List<IMethodBinding> usedSuperMethods) {
		this.usedSuperMethods = usedSuperMethods;
	}
	public int getNOM() {
		return NOM;
	}
	public void setNOM(int nOM) {
		NOM = nOM;
	}
	public int getWMC() {
		return WMC;
	}
	public double getAMW() {
		return AMW;
	}
	public void setAMW(double aMW) {
		AMW = aMW;
	}
	public int getNProtM() {
		return NProtM;
	}
	public void setNProtM(int nProtM) {
		NProtM = nProtM;
	}
	public double getBOvR() {
		return BOvR;
	}
	public void setBOvR(double bOvR) {
		if(NOM != 0) BOvR = bOvR/NOM;
	}
	public int getNAS() {
		return NAS;
	}
	public void setNAS(int nAS) {
		NAS = pmlist.size() - nAS;
	}
	public double getPNAS() {
		return PNAS;
	}
	public void setPNAS() {
		if(pmlist.size() != 0)PNAS = (double)NAS/pmlist.size();
	}
	public double getBUR() {
		return BUR;
	}
	public void setBUR(int bUR) {
		if(bUR != 0)BUR = (double)(usedSuperFields.size()+usedSuperMethods.size())/bUR;
	}
	public Chapter7Data getParent() {
		return parent;
	}
	public void setParent(Chapter7Data parent) {
		this.parent = parent;
	}
	public void setWMC(int wMC) {
		WMC = wMC;
	}
	public void setPmlist(List<MethodDeclaration> pmlist) {
		this.pmlist = pmlist;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public List<MethodDeclaration> getMlist() {
		return mlist;
	}
	public void setMlist(List<MethodDeclaration> mList) {
		this.mlist = mList;
	}
	public List<MethodDeclaration> getPmlist() {
		return pmlist;
	}
	public void setPmd(List<MethodDeclaration> pmList) {
		this.pmlist = pmList;
	}
	public List<MethodDeclaration> getIsmlist() {
		return ismlist;
	}
	public void setIsmlist(List<MethodDeclaration> ismlist) {
		this.ismlist = ismlist;
	}
	public List<FieldDeclaration> getAlist() {
		return alist;
	}
	public void setAlist(List<FieldDeclaration> alist) {
		this.alist = alist;
	}
	public List<FieldDeclaration> getIsalist() {
		return isalist;
	}
	public void setIsalist(List<FieldDeclaration> isalist) {
		this.isalist = isalist;
	}
	
	public boolean isRPB() {
		return ((NProtM > 3 && BUR < (double)1/3) || BOvR < (double)1/3)
				&& ((AMW > 2.0 || WMC > 14) && NOM > 7);
	}
	
	public boolean isTB() {
		return (NAS >= 7 && PNAS < (double)2/3)
		&& ((AMW > 2.0 || WMC >= 47) && NOM >= 10)
		&& (AMW > 2.0 && NOM > 5 && WMC >= 14);
	}
	
	public boolean isParent(String p) {
		if(pname != null) return pname.matches(p);
		else return false;
	}
	
	public void addChild(Chapter7Data child) {
		childs.add(child);
	}
	
	public void outData() {
		System.out.println(name);
		if(pname != null)System.out.println(pname);
		System.out.println("NOM:"+NOM);
		System.out.println("WMC:"+WMC);
		System.out.println("AMW:"+AMW);
		System.out.println("NProtM:"+NProtM);
		System.out.println("BOvR:"+BOvR);
		System.out.println("NAS:"+NAS);
		System.out.println("PNAS:"+PNAS);
		System.out.println("BUR:"+BUR);
		System.out.println("Refused Parent Bequest:"+this.isRPB());
		System.out.println("Tradition Breaker:"+this.isTB());
		System.out.println();
		
	}
	
}
