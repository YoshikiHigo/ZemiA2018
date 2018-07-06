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
	public String name;
	public String pname;
	List<MethodDeclaration> md = new ArrayList<MethodDeclaration>();//method list
	List<MethodDeclaration> pmd = new ArrayList<MethodDeclaration>();//public method list
	List<MethodDeclaration> ismlist = new ArrayList<MethodDeclaration>();//public or protected method list
	List<FieldDeclaration> alist = new ArrayList<FieldDeclaration>();//fieldlist
	List<FieldDeclaration> isalist = new ArrayList<FieldDeclaration>();//public or protected field list
}
