package zemiA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.WhileStatement;

public class ch7Visitor extends ASTVisitor {
	int AMW;//WMC/NOM
	int NOO;//Number of Override
	int BOvR;//NOO/NOM
	int NOI;//Number of inheritance-specific members used
	int NOU;//Number of inheritance-specific members
	int NAS;//Number of public methods that are not overridden from the base class(NOP-pmlist)
	int NOP;//Number of public methods
	int PNAS;//NAS/Parent's NOP
	List<String> pmlist = new ArrayList<String>();//public methods
	List<String> islist = new ArrayList<String>();//inheritance-specific members
	List<String> isusedlist = new ArrayList<String>();//inheritance-specific members used
	List<ch7Data> cDataList = new ArrayList<ch7Data>();//data for each method 
	ch7Data cData;
	double sumofNOM=0;
	double sumofWMC=0;
	double aveNOM=0, aveWMC=0, aveAMW=0;
	
	
	@Override
	public boolean visit(TypeDeclaration node) {
		// TODO Auto-generated method stub
		cData = new ch7Data();
		cData.name = node.getName().toString();
		//System.out.println(name);
		Type snode = node.getSuperclassType();
		if(snode != null) {
			cData.pname = snode.toString();
			//System.out.println(pname);
		}
		MethodDeclaration[] m = node.getMethods();
		cData.md.addAll(Arrays.asList(m));
		return super.visit(node);
	}


	@Override
	public void endVisit(TypeDeclaration node) {
		// TODO Auto-generated method stub
		cDataList.add(cData);
		sumofNOM+=cData.NOM;
		sumofWMC+=cData.CYCLO;
		super.endVisit(node);
	}


	@Override
	public void endVisit(CompilationUnit node) {
		// TODO Auto-generated method stub
		aveNOM=sumofNOM/cDataList.size();
		aveWMC=sumofWMC/cDataList.size();
		aveAMW=(sumofWMC/sumofNOM)/cDataList.size();
		node.accept(new ch7Judge(cDataList, aveNOM, aveWMC, aveAMW));
		super.endVisit(node);
	}


	@Override
	public boolean visit(MethodDeclaration node) {
		// TODO Auto-generated method stub
		cData.NOM++;
		if((Modifier.PUBLIC & node.getModifiers()) != 0) {
			cData.pmd.add(node);
			cData.ismlist.add(node);
		}else if((Modifier.PROTECTED & node.getModifiers()) != 0){
			cData.NProtM++;
			cData.ismlist.add(node);
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		// TODO Auto-generated method stub
		if((Modifier.PUBLIC & node.getModifiers()) != 0) {
			cData.isalist.add(node);
		}else if((Modifier.PROTECTED & node.getModifiers()) != 0){
			cData.isalist.add(node);
			cData.NProtM++;
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(ForStatement node) {
		// TODO Auto-generated method stub
		cData.CYCLO++;
		return super.visit(node);
	}
	@Override
	public boolean visit(IfStatement node) {
		// TODO Auto-generated method stub
		cData.CYCLO++;
		return super.visit(node);
	}
	@Override
	public boolean visit(SwitchStatement node) {
		// TODO Auto-generated method stub
		cData.CYCLO++;
		return super.visit(node);
	}
	@Override
	public boolean visit(WhileStatement node) {
		// TODO Auto-generated method stub
		cData.CYCLO++;
		return super.visit(node);
	}

	
	
}
