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
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ZemiAVisitor extends ASTVisitor {
	int NOM = 0;
	int CYCLO = 0;//WMC, complexity
	int AMW;//WMC/NOM
	int NOO;//Number of Override
	int BOvR;//NOO/NOM
	int NOI;//Number of inheritance-specific members used
	int NOU;//Number of inheritance-specific members
	int NAS;//Number of public methods that are not overridden from the base class(NOP-pmlist)
	int NOP;//Number of public methods
	int PNAS;//NAS/Parent's NOP
	List<String> pmlist = new ArrayList<String>();//public methods
	List<String> pmusedlist = new ArrayList<String>();//public methods overridden
	List<String> islist = new ArrayList<String>();//inheritance-specific members
	List<String> isusedlist = new ArrayList<String>();//inheritance-specific members used
	String name;
	String pname = null;
	int i= 0;
	List<MethodDeclaration> md = new ArrayList<MethodDeclaration>();
	
	
	@Override
	public boolean visit(CompilationUnit node) {
		// TODO Auto-generated method stub
		//node.get
		
		return super.visit(node);
	}




	@Override
	public boolean visit(TypeDeclaration node) {
		
		MethodDeclaration[] m = node.getMethods();
		md.addAll(Arrays.asList(m));
		//System.arraycopy(m, 0, md, 0, m.length);
		//System.arraycopy(m, 0, md, md.length, m.length);
		//System.out.println(node.getParent());
		
		Type snode;
		// TODO Auto-generated method stub
		name = node.getName().toString();
		System.out.println(name+":");
		snode = node.getSuperclassType();
		if(snode != null) {
			pname = snode.toString();
			//System.out.println(pname);
		}
		return super.visit(node);
	}

	
	
	
	@Override
	public boolean visit(MethodInvocation node) {
		// TODO Auto-generated method stub
		//ZemiAMain.a();
		for(MethodDeclaration m :md) {
			boolean a = m.resolveBinding().equals((node.resolveMethodBinding()));
			if(a)System.out.println(m.getName().toString() + a);
		}
		
		if(null == node.resolveMethodBinding().getDeclaringClass())System.out.println("11111111111111111111111111111111");
		
		return super.visit(node);
	}




	@Override
	public boolean visit(MethodDeclaration node) {
		
		//System.out.println(node);
		// TODO Auto-generated method stub
		//System.out.println(node.getReceiverType());
		//System.out.println(node.getReceiverQualifier());
		//System.out.println(node.getJavadoc());
		//System.out.println(node.getExtraDimensions());
		//System.out.println(node.typeParameters());
		//System.out.println(node.parameters());
		//System.out.println(node.modifiers());
		//System.out.println(node.getReturnType2());
		
		
		
		NOM++;
		if((Modifier.PUBLIC & node.getModifiers()) != 0) {
			NOP++;
			pmlist.add(node.getName().toString());
			islist.add(node.getName().toString());
		}else if((Modifier.PROTECTED & node.getModifiers()) != 0){
			islist.add(node.getName().toString());
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperFieldAccess node) {
		// TODO Auto-generated method stub
		isusedlist.add(node.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperMethodInvocation node) {//yobidasi
		// TODO Auto-generated method stub
		isusedlist.add(node.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperMethodReference node) {//
		// TODO Auto-generated method stub
		isusedlist.add(node.toString());
		return super.visit(node);
	}

	@Override
	public void endVisit(TypeDeclaration node) {
		// TODO Auto-generated method stub
		//System.out.println(primlist.toString());
		//System.out.println(islist.toString());
		//System.out.println(isusedlist.toString());
		super.endVisit(node);
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		// TODO Auto-generated method stub
		if((Modifier.PUBLIC & node.getModifiers()) != 0) {
			islist.add(node.fragments().toString());
		}else if((Modifier.PROTECTED & node.getModifiers()) != 0){
			islist.add(node.getType().toString());
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(ForStatement node) {
		// TODO Auto-generated method stub
		CYCLO++;
		return super.visit(node);
	}

	@Override
	public boolean visit(IfStatement node) {
		// TODO Auto-generated method stub
		CYCLO++;
		return super.visit(node);
	}

	@Override
	public boolean visit(SwitchStatement node) {
		// TODO Auto-generated method stub
		CYCLO++;	
		return super.visit(node);
	}



	
	
}
