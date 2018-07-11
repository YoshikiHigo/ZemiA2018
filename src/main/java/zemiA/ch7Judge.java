package zemiA;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.TypeDeclaration;


public class ch7Judge extends ASTVisitor{
	List<ch7Data> ch7Data;
	ch7Data cData, pData;
	List<String> usedSuperFields;
	List<IMethodBinding> usedSuperMethods;
	double aveNOM, aveWMC, aveAMW;
	
	ch7Judge(List<ch7Data> list, double NOM, double WMC, double AMW) {
		ch7Data = list;
		aveNOM=NOM;
		aveWMC=WMC;
		aveAMW=AMW;
	}
	@Override
	public boolean visit(TypeDeclaration node) {
		// TODO Auto-generated method stub
		int i = 0;
		pData = null;
		usedSuperFields = new ArrayList<String>();
		usedSuperMethods = new ArrayList<IMethodBinding>();
		
		for(ch7Data Data : ch7Data) {
			if(node.getName().toString().matches(Data.name)) cData = Data;
			else if(node.getSuperclassType()!=null && node.getSuperclassType().toString().matches(Data.name)) pData = Data;
		}
		
		if(cData.NOM != 0) cData.AMW = cData.CYCLO/(double)cData.NOM;
		
		if(pData !=null) {
			if(cData.md !=null && pData.ismlist !=null) {
				for(MethodDeclaration cm :cData.md) {
					for(MethodDeclaration pm :pData.ismlist) {
						if(cm.resolveBinding().overrides(pm.resolveBinding()))i++;
					}
				}
				if(cData.NOM != 0) cData.BOvR=(double)i/(double)cData.NOM;
			}
			i=0;
			
			if(cData.pmd != null && pData.ismlist !=null) {
				for(MethodDeclaration cm :cData.pmd) {
					for(MethodDeclaration pm :pData.ismlist) {
						if(cm.resolveBinding().overrides(pm.resolveBinding())) i++;
					}
				}
			}
			
			cData.NAS = cData.pmd.size() - i;
			cData.PNAS = (double)cData.NAS/(double)cData.pmd.size();

		}
		
		System.out.println(cData.name);
		if(pData!=null)System.out.println(pData.name);
		System.out.println("NOM:"+cData.NOM);
		System.out.println("WMC:"+cData.CYCLO);
		System.out.println("AMW:"+cData.AMW);
		System.out.println("NProtM:"+cData.NProtM);
		System.out.println("BOvR:"+cData.BOvR);
		System.out.println("NAS:"+cData.NAS);
		System.out.println("PNAS:"+cData.PNAS);
		return super.visit(node);
	}
	
	@Override
	public void endVisit(TypeDeclaration node) {
		// TODO Auto-generated method stub
		if(pData != null) 
		cData.BUR = (double)(usedSuperFields.size() + usedSuperMethods.size())/(double)(pData.isalist.size() + pData.ismlist.size());
		
		System.out.println("BUR:"+cData.BUR);
		
		if(pData != null) {
			if(((cData.NProtM > 3 && cData.BUR < (double)1/3) || cData.BOvR < (double)1/3)
					&& ((cData.AMW > aveAMW || cData.CYCLO > aveWMC) && cData.NOM > aveNOM)) System.out.println("Refused Parent Bequest");
			if((cData.NAS >= aveNOM && cData.PNAS < (double)2/3)
					&& ((cData.AMW > aveAMW || cData.CYCLO >= 2*aveWMC) && cData.NOM >= 3*aveNOM/2)
					&& (pData.AMW > aveAMW && pData.NOM > 3*aveNOM/4 && cData.CYCLO >= aveWMC)) System.out.println("Tradition Breaker");
		}
		
		
		System.out.println();
		super.endVisit(node);
	}
	@Override
	public boolean visit(SuperFieldAccess node) {
		// TODO Auto-generated method stub
		if(usedSuperFields.contains(node.getName().toString()));
		else usedSuperFields.add(node.getName().toString());
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

}
