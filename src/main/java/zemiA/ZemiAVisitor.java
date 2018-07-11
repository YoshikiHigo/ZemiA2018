package zemiA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.WhileStatement;

public class ZemiAVisitor extends ASTVisitor {
	/*
	 *  This class search java class's tree over the project
	 *  endVisit(TypeDeclaration): finish searching previous class's tree and prepare to serch next class's tree
	 *  endVidit(CompilationUnit): finish searching java project
	 */

	private int classNesting = 0;
	private int classMaxNesting = 0;
	private int methodNesting = 0;
	private int methodMaxNesting = 0;
	private List<IMethodBinding> classMethods;
	private List<ClassInformation> allDeclaratedClasses = new ArrayList<ClassInformation>();  //project's classes
	private List<MethodInformation> allDeclaratedMethods = new ArrayList<MethodInformation>();  //project's methods
	private List<MethodInvocation> allInvokedMethods = new ArrayList<MethodInvocation>();
	private HashMap<MethodInvocation,MethodDeclaration> allInvokingMethods =
			new HashMap<MethodInvocation,MethodDeclaration>();  //method invocation is unique
	private Stack<MethodDeclaration> methodDeclarationStack = new Stack<MethodDeclaration>();

	private List<IVariableBinding> usedSuperFields;
	private List<IMethodBinding> usedSuperMethods;
	private int NOM = 0;
	private int WMC = 0;//=WMC, complexity
	private int NProtM = 0;
	//private List<MethodDeclaration> mlist;//method list
	private List<MethodDeclaration> pmlist;//public method list
	private List<MethodDeclaration> ismlist;//public or protected method list
	private List<FieldDeclaration> alist;//fieldlist
	private List<FieldDeclaration> isalist;//public or protected field list






	@Override
	public void endVisit(CompilationUnit node) {
		// Judgement over Project by List

		//chapter 6
		for(MethodInvocation invokedMethod: allInvokedMethods) {
			IMethodBinding invokedMethodBind = invokedMethod.resolveMethodBinding();
			IMethodBinding invokingMethodBind = allInvokingMethods.get(invokedMethod).resolveBinding();
			ClassInformation invokingClass = getClassInformation(invokingMethodBind.getDeclaringClass());
			MethodInformation invokingMethod = getMethodInformation(invokingMethodBind);
			if(isDistinctMethod(invokingClass, invokedMethodBind) && isProjectMethod(invokedMethodBind)) {
				invokingMethod.invocate(invokedMethodBind);
				getMethodInformation(invokedMethodBind).invocated(invokingMethodBind);
			}
		}

		//chapter 7
		for(ClassInformation Data : allDeclaratedClasses) {
			ClassInformation pData = getClassInformation(Data.getParentBindig());
			if(pData != null) {
				Data.setParentClass(pData);
				pData.addChildClass(Data);
				double ovnum = 0;
				int povnum = 0;

				for(IMethodBinding m :Data.getMethodsList()) {
					for(MethodDeclaration pm :pData.getIsmlist()) {
						if(m.overrides(pm.resolveBinding()))ovnum++;
					}
				}
				for(MethodDeclaration m :Data.getPmlist()) {
					for(MethodDeclaration pm :pData.getIsmlist()) {
						if(m.resolveBinding().overrides(pm.resolveBinding())) povnum++;
					}
				}
				Data.setInheritanceInformation(ovnum, povnum);
			}
		}



		// Print Class Informations
		System.out.println("print class informations: ");
		for(ClassInformation classInformation: allDeclaratedClasses) {
			classInformation.printClassInformation();

		}

		// Print Project Information
		System.out.println("print project informations: ");
//		for(MethodInformation m: getShotgunSurgeryMethodList()) {
//			System.out.println(m.getMethodBinding().getName().toString()); //TODO why this line comment out cause bag?
//			m.printMethodInfomation(MethodInformation.FOR_DISPLAY);
//		}
		for(MethodInformation m: allDeclaratedMethods) {
			//System.out.println(m.getMethodBinding().getName().toString()); //TODO why this line comment out cause bag?
			m.printMethodInfomation();
		}
		System.out.println();

		super.endVisit(node);
	}

	private boolean isDistinctMethod(ClassInformation invokingClass, IMethodBinding methodBind) {
		// if distinct method contains() returns false
		 List<IMethodBinding> invokingClassMethods = invokingClass.getMethodsList();
		return !invokingClassMethods.contains(methodBind);
	}

	private boolean isProjectMethod(IMethodBinding methodBind) {
		boolean includeFlag = false;
		for(MethodInformation declaratedMethod: allDeclaratedMethods) {
			includeFlag = declaratedMethod.getMethodBinding().equals(methodBind);
			if(includeFlag == true) {
				break;
			}
		}
		return includeFlag;
	}

	private MethodInformation getMethodInformation(IMethodBinding methodBind) {
		for(MethodInformation methodInformation: allDeclaratedMethods) {
			if(methodInformation.getMethodBinding().equals(methodBind)) {
				return methodInformation;
			}
		}
		// if the method is not project method
		return null;
	}

	private ClassInformation getClassInformation(ITypeBinding classBind) {
		if(classBind != null) {
			for(ClassInformation classInformation: allDeclaratedClasses) {
				if(classInformation.getClassBinding().equals(classBind)) {
					return classInformation;
				}
			}
		}
		return null;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		// prepare to search next class :Initialize
		classMaxNesting = 0;
		classMethods = new ArrayList<IMethodBinding>();
		//allDeclaratedClasses.add(new ClassInformation(node.resolveBinding()));

		NOM = 0;
		WMC = 0;//=WMC, complexity
		NProtM = 0;
		pmlist = new ArrayList<MethodDeclaration>();//public method list
		ismlist = new ArrayList<MethodDeclaration>();//public or protected method list
		alist = new ArrayList<FieldDeclaration>();//fieldlist
		isalist = new ArrayList<FieldDeclaration>();//public or protected field list
		usedSuperFields = new ArrayList<IVariableBinding>();
		usedSuperMethods = new ArrayList<IMethodBinding>();

		for(MethodDeclaration mb: node.getMethods()) {
			classMethods.add(mb.resolveBinding());
			allDeclaratedMethods.add(new MethodInformation(mb.resolveBinding()));
		}

		return super.visit(node);
	}

	@Override
	public void endVisit(TypeDeclaration node) {
		// set searched class information
		ClassInformation checkedClass = new ClassInformation(node.resolveBinding());
		allDeclaratedClasses.add(checkedClass);

		checkedClass.setClassInformation(classMethods ,classMaxNesting , NOM, WMC, NProtM,
				 pmlist, ismlist, alist, isalist, usedSuperFields, usedSuperMethods);

		ITypeBinding stb = node.resolveBinding().getSuperclass();
		if(stb != null) {
			checkedClass.setParent(stb);
		}

		super.endVisit(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		// to access from MethodInvocation to MethodDeclaration
		methodDeclarationStack.push(node);

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
	public void endVisit(MethodDeclaration node) {
		// to access from MethodInvocation to MethodDeclaration
		methodDeclarationStack.pop();
		getMethodInformation(node.resolveBinding()).setMaxNesting(methodMaxNesting);
		methodMaxNesting = 0;
		methodNesting = 0;
		super.endVisit(node);
	}

	@Override
	public boolean visit(MethodInvocation node) {
		allInvokedMethods.add(node);
		allInvokingMethods.put(node, methodDeclarationStack.peek());
		return super.visit(node);
	}

	@Override
	public boolean visit(Block node) {
		classNesting++;
		methodNesting++;
		if(methodMaxNesting < methodNesting) {
			methodMaxNesting = methodNesting;
		}
		if(classMaxNesting < classNesting) {
			classMaxNesting = classNesting;
		}
		return super.visit(node);
	}

	@Override
	public void endVisit(Block node) {
		classNesting--;
		methodNesting--;
		super.endVisit(node);
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


	public List<MethodInformation> getShotgunSurgeryMethodList(){
		List<MethodInformation> shotgunSurgeryMethods = new ArrayList<MethodInformation>();
		for(MethodInformation methodInformation: allDeclaratedMethods) {
			if(methodInformation.isShotgunSurgery()) {
				shotgunSurgeryMethods.add(methodInformation);
			}
		}
		return shotgunSurgeryMethods;
	}

}