package zemiA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.WhileStatement;

public class Chapter7Visitor extends ASTVisitor {
	private List<Chapter7Data> DataList = new ArrayList<Chapter7Data>();//data for each method 
	private Chapter7Data Data;
	private List<IVariableBinding> usedSuperFields;
	private List<IMethodBinding> usedSuperMethods;
	private int NOM = 0;
	private int WMC = 0;//=WMC, complexity
	private int NProtM = 0;
	private String name;
	private String pname;
	private List<MethodDeclaration> mlist;//method list
	private List<MethodDeclaration> pmlist;//public method list
	private List<MethodDeclaration> ismlist;//public or protected method list
	private List<FieldDeclaration> alist;//fieldlist
	private List<FieldDeclaration> isalist;//public or protected field list
	
	@Override
	public boolean visit(TypeDeclaration node) {
		// TODO Auto-generated method stub
		Data = new Chapter7Data();
		NOM = 0;
		WMC = 0;//=WMC, complexity
		NProtM = 0;
		mlist = new ArrayList<MethodDeclaration>();//method list
		pmlist = new ArrayList<MethodDeclaration>();//public method list
		ismlist = new ArrayList<MethodDeclaration>();//public or protected method list
		alist = new ArrayList<FieldDeclaration>();//fieldlist
		isalist = new ArrayList<FieldDeclaration>();//public or protected field list
		usedSuperFields = new ArrayList<IVariableBinding>();
		usedSuperMethods = new ArrayList<IMethodBinding>();
		
		
		ITypeBinding tb = node.resolveBinding();
		ITypeBinding stb = tb.getSuperclass();
		name = tb.getBinaryName();
		Data.setName(name);
		//System.out.println(name);
		if(stb != null) {
			pname = stb.getBinaryName();
			Data.setPname(pname);
			//System.out.println(pname);
		}
		MethodDeclaration[] m = node.getMethods();
		mlist.addAll(Arrays.asList(m));
		Data.setMlist(mlist);
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		// TODO Auto-generated method stub
		NOM++;
		if((Modifier.PUBLIC & node.getModifiers()) != 0) {
			pmlist.add(node);
			ismlist.add(node);
		}else if((Modifier.PROTECTED & node.getModifiers()) != 0){
			NProtM++;
			ismlist.add(node);
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		// TODO Auto-generated method stub
		if((Modifier.PUBLIC & node.getModifiers()) != 0) {
			isalist.add(node);
		}else if((Modifier.PROTECTED & node.getModifiers()) != 0){
			isalist.add(node);
			NProtM++;
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(ForStatement node) {
		// TODO Auto-generated method stub
		WMC++;
		return super.visit(node);
	}
	@Override
	public boolean visit(IfStatement node) {
		// TODO Auto-generated method stub
		WMC++;
		return super.visit(node);
	}
	@Override
	public boolean visit(SwitchStatement node) {
		// TODO Auto-generated method stub
		WMC++;
		return super.visit(node);
	}
	@Override
	public boolean visit(WhileStatement node) {
		// TODO Auto-generated method stub
		WMC++;
		return super.visit(node);
	}
	@Override
	public boolean visit(DoStatement node) {
		// TODO Auto-generated method stub
		WMC++;
		return super.visit(node);
	}
	@Override
	public boolean visit(SuperFieldAccess node) {
		// TODO Auto-generated method stub
		if(usedSuperFields.contains(node.resolveFieldBinding()));
		else usedSuperFields.add(node.resolveFieldBinding());
		return super.visit(node);
	}
	@Override
	public boolean visit(SuperMethodInvocation node) {
		// TODO Auto-generated method stub
		if(usedSuperMethods.contains(node.resolveMethodBinding()));
		else usedSuperMethods.add(node.resolveMethodBinding());
		return super.visit(node);
	}
	@Override
	public boolean visit(SuperMethodReference node) {
		// TODO Auto-generated method stub
		if(usedSuperMethods.contains(node.resolveMethodBinding()));
		else usedSuperMethods.add(node.resolveMethodBinding());
		return super.visit(node);
	}
	
	@Override
	public void endVisit(TypeDeclaration node) {
		// TODO Auto-generated method stub
		Data.setNOM(NOM);
		Data.setWMC(WMC);
		if(NOM != 0) Data.setAMW((double)WMC/(double)NOM);
		Data.setNProtM(NProtM);
		Data.setAlist(alist);
		Data.setIsalist(isalist);
		Data.setPmlist(pmlist);
		Data.setIsmlist(ismlist);
		Data.setUsedSuperFields(usedSuperFields);
		Data.setUsedSuperMethods(usedSuperMethods);
		DataList.add(Data);
		super.endVisit(node);
	}
	
	@Override
	public void endVisit(CompilationUnit node) {
		// TODO Auto-generated method stub
		
		int i = 0;
		for(Chapter7Data Data : DataList) {
			for(Chapter7Data pData : DataList) {
				if(Data.isParent(pData.getName())) {
					Data.setParent(pData);
					pData.addChild(Data);
	
					for(MethodDeclaration m :Data.getMlist()) {
						for(MethodDeclaration pm :pData.getIsmlist()) {
							if(m.resolveBinding().overrides(pm.resolveBinding()))i++;
						}
					}
					Data.setBOvR((double)i);
					
					i=0;
					
						for(MethodDeclaration m :Data.getPmlist()) {
							for(MethodDeclaration pm :pData.getIsmlist()) {
								if(m.resolveBinding().overrides(pm.resolveBinding())) i++;
							}
						}
					
					Data.setNAS(i);
					Data.setPNAS();
					
					Data.setBUR((pData.getIsalist().size() + pData.getIsmlist().size()));
				}
			}
			Data.outData();
		}		
		super.endVisit(node);
	}
}

