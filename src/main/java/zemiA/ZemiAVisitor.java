package zemiA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ZemiAVisitor extends ASTVisitor {
	/*
	 *  This class search java class's tree over the project
	 *  endVisit(TypeDeclaration): finish searching previous class's tree and prepare to serch next class's tree
	 *  endVidit(CompilationUnit): finish searching java project
	 */

	private int nesting = 0;
	private int maxNesting = 0;
	private List<IMethodBinding> classMethods;
	private List<ClassInformation> allDeclaratedClasses = new ArrayList<ClassInformation>();
	private List<MethodInformation> allDeclaratedMethods = new ArrayList<MethodInformation>();
	private List<MethodInvocation> allInvokedMethods = new ArrayList<MethodInvocation>();
	private HashMap<MethodInvocation,MethodDeclaration> allInvokingMethods =
			new HashMap<MethodInvocation,MethodDeclaration>();  //method invocation is unique
	private Stack<MethodDeclaration> methodDeclarationStack = new Stack<MethodDeclaration>();

	@Override
	public void endVisit(CompilationUnit node) {
		// Judgement over Project by List
		for(MethodInvocation invokedMethod: allInvokedMethods) {
			IMethodBinding invokedMethodBind = invokedMethod.resolveMethodBinding();
			IMethodBinding invokingMethodBind = allInvokingMethods.get(invokedMethod).resolveBinding();
			ClassInformation invokingClass = getClassInformation(invokingMethodBind.getDeclaringClass());
			if(isDistinctMethod(invokingClass, invokedMethodBind) && isProjectMethod(invokedMethodBind)) {
				invokingClass.invocate(invokedMethodBind);
				getMethodInformation(invokedMethodBind).invocated(invokingMethodBind);
			}
		}

		// Print Class Informations
		System.out.println("print class informations: ");
		for(ClassInformation classInformation: allDeclaratedClasses) {
			classInformation.printClassInformation();
		}

		// Print Project Information
		System.out.println("shotgun surgery method list: ");
		for(MethodInformation m: getShotgunSurgeryMethodList()) {
			System.out.println(m.getMethodBinding().getName().toString()); //why this line comment out cause bag?
			m.printMethodInfomation();
		}
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
		for(ClassInformation classInformation: allDeclaratedClasses) {
			if(classInformation.getClassBinding().equals(classBind)) {
				return classInformation;
			}
		}
		return null;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		// prepare to search next class :Initialize
		maxNesting = 0;
		classMethods = new ArrayList<IMethodBinding>();
		allDeclaratedClasses.add(new ClassInformation(node.resolveBinding()));

		for(MethodDeclaration mb: node.getMethods()) {
			classMethods.add(mb.resolveBinding());
			allDeclaratedMethods.add(new MethodInformation(mb.resolveBinding()));
		}
		return super.visit(node);
	}

	@Override
	public void endVisit(TypeDeclaration node) {
		// set searched class information
		ClassInformation searchedClass = getClassInformation(node.resolveBinding());
		searchedClass.setMethodList(classMethods);
		searchedClass.setMaxNesting(maxNesting);
		super.endVisit(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		// to access from MethodInvocation to MethodDeclaration
		methodDeclarationStack.push(node);
		return super.visit(node);
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		// to access from MethodInvocation to MethodDeclaration
		methodDeclarationStack.pop();
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
		nesting++;
		if(maxNesting < nesting) {
			maxNesting = nesting;
		}
		return super.visit(node);
	}

	@Override
	public void endVisit(Block node) {
		nesting--;
		super.endVisit(node);
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
