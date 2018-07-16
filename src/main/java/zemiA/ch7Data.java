package zemiA;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class ch7Data {
	public int NOM = 0;
	public int CYCLO = 0;//=WMC, complexity
	public double AMW = -1;
	public int NProtM = 0;
	public double BOvR = -1;
	public int NAS = -1;
	public double PNAS = -1;
	public double BUR = -1;
	public boolean RefusedParentBequest = false;
	public boolean TraditionBreaker = false;
	public String name;
	public String pname;
	List<MethodDeclaration> md = new ArrayList<MethodDeclaration>();//method list
	List<MethodDeclaration> pmd = new ArrayList<MethodDeclaration>();//public method list
	List<MethodDeclaration> ismlist = new ArrayList<MethodDeclaration>();//public or protected method list
	List<FieldDeclaration> alist = new ArrayList<FieldDeclaration>();//fieldlist
	List<FieldDeclaration> isalist = new ArrayList<FieldDeclaration>();//public or protected field list
	public int getNOM() {
		return NOM;
	}
	public void setNOM(int nOM) {
		NOM = nOM;
	}
	public int getCYCLO() {
		return CYCLO;
	}
	public void setCYCLO(int cYCLO) {
		CYCLO = cYCLO;
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
		BOvR = bOvR;
	}
	public int getNAS() {
		return NAS;
	}
	public void setNAS(int nAS) {
		NAS = nAS;
	}
	public double getPNAS() {
		return PNAS;
	}
	public void setPNAS(double pNAS) {
		PNAS = pNAS;
	}
	public double getBUR() {
		return BUR;
	}
	public void setBUR(double bUR) {
		BUR = bUR;
	}
	public boolean isRefusedParentBequest() {
		return RefusedParentBequest;
	}
	public void setRefusedParentBequest(boolean refusedParentBequest) {
		RefusedParentBequest = refusedParentBequest;
	}
	public boolean isTraditionBreaker() {
		return TraditionBreaker;
	}
	public void setTraditionBreaker(boolean traditionBreaker) {
		TraditionBreaker = traditionBreaker;
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
	public List<MethodDeclaration> getMd() {
		return md;
	}
	public void setMd(List<MethodDeclaration> md) {
		this.md = md;
	}
	public List<MethodDeclaration> getPmd() {
		return pmd;
	}
	public void setPmd(List<MethodDeclaration> pmd) {
		this.pmd = pmd;
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
				&& ((AMW > 2.0 || CYCLO > 14) && NOM > 7);
	}
	
	public boolean isTB() {
		return (NAS >= 7 && PNAS < (double)2/3)
		&& ((AMW > 2.0 || CYCLO >= 47) && NOM >= 10)
		&& (AMW > 2.0 && NOM > 5 && CYCLO >= 14);
	}
	
}
