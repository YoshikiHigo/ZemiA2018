package zemiA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ZemiAVisitor extends ASTVisitor {
	/*
	 *  This class search java class's tree over the project
	 *  endVisit(TypeDeclaration): finish searching previous class's tree and prepare to serch next class's tree
	 *  endVidit(CompilationUnit): finish searching java project
	 */
	/*===========*/
	private int privatefields;

	CompilationUnit unit;
	private String methodname;
	private Map<String,Boolean> fieldmap= new HashMap<String,Boolean>();
	private ArrayList<String> methodlist = new ArrayList<String>();
	private Map<String,Boolean> accessormethodmap= new HashMap<String,Boolean>();
	private Map<String,Integer> methodLOCmap= new HashMap<String,Integer>();
	private int publicFunctionalMethod;
	private int publicMethod;
	private int woc;
	/*=================*/

	private int nesting = 0;
	private int maxNesting = 0;
	private List<IMethodBinding> classMethods;
	private List<MethodInformation> allDeclaratedMethods = new ArrayList<MethodInformation>();
	private List<MethodInvocation> allInvokedMethods = new ArrayList<MethodInvocation>();
	private HashMap<MethodInvocation,MethodDeclaration> allInvokingMethods =
			new HashMap<MethodInvocation,MethodDeclaration>();  //method invocation is unique
	private Stack<MethodDeclaration> methodDeclarationStack = new Stack<MethodDeclaration>();
	private List<ClassInformation> allDeclaratedClasses = new ArrayList<ClassInformation>();
	private List<MethodInformation> shotgunSurgeryMethods = new ArrayList<MethodInformation>();

	@Override
	public void endVisit(CompilationUnit node) {
		// Judgement
		for(MethodInvocation invokedMethod: allInvokedMethods) {
			IMethodBinding invokedMethodBind = invokedMethod.resolveMethodBinding();
			IMethodBinding invokingMethodBind = allInvokingMethods.get(invokedMethod).resolveBinding();
			ClassInformation invokingClass = getClassInformation(invokingMethodBind.getDeclaringClass());

			if(isDistinctMethod(invokingClass, invokedMethodBind) && isProjectMethod(invokedMethodBind)) {
				invokingClass.invocate(invokedMethodBind);
				getMethodInformation(invokedMethodBind).invocated(invokingMethodBind);
			}

			/*==================*/
			for(String method : methodlist) {
				System.out.println(method+"="+accessormethodmap.get(method));
			}
			for (String method : methodlist) {
				System.out.println(method + "=" + accessormethodmap.get(method));
			}
			System.out.print("number of public method = ");
			System.out.println(this.publicMethod);
			System.out.print("number of public functional method = ");
			System.out.println(this.publicFunctionalMethod);
			if (this.publicMethod != 0) {
				this.woc = this.publicFunctionalMethod / this.publicMethod;
			}
			System.out.print("WOC = ");
			System.out.println(this.woc);
			/*==================*/
		}

		// Print Class Informations
		for(ClassInformation classInformation: allDeclaratedClasses) {
			classInformation.printClassInformation();
		}

		// Print Project Information
		System.out.println("Project: ");
		System.out.println("shotgun surgery method: ");
		for(MethodInformation m: getShotgunSurgeryMethodList()) {
			System.out.println(m.getMethodBinding().getName().toString());
		}
		for(String method : methodlist) {
			System.out.println(method+"LOC="+methodLOCmap.get(method));
		}
		super.endVisit(node);
	}

	private boolean isDistinctMethod(ClassInformation invokingClass, IMethodBinding methodBind) {
		// if distinct method contains() returns false
		List<IMethodBinding> invokingClassMethods = invokingClass.getMethodsList();
		return !invokingClassMethods.contains(methodBind);
	}

	/*always return false: Developping...*/
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
		/*===============*/
		this.publicMethod++;
		this.publicFunctionalMethod++;
		System.out.print(node.getName() + " method LOC = ");
		System.out.println(unit.getLineNumber(node.getStartPosition() + node.getLength() - 1) + 1
				- unit.getLineNumber(node.getStartPosition()));
		methodname = node.getName().toString();
		accessormethodmap.put(methodname, Boolean.FALSE);
		methodlist.add(methodname);
		int methodloc=unit.getLineNumber(node.getStartPosition() + node.getLength() -1)+1-unit.getLineNumber(node.getStartPosition());
		methodLOCmap.put(methodname, methodloc);
		/*==============*/
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
		for(MethodInformation methodInformation: allDeclaratedMethods) {
			if(methodInformation.isShotgunSurgery()) {
				shotgunSurgeryMethods.add(methodInformation);
			}
		}
		return shotgunSurgeryMethods;
	}

	/*==================*/



	@Override
	public boolean visit(ReturnStatement node) {
		// System.out.println(node.getExpression());
		if (node.getExpression() == null) {

		} else if (node.getExpression().toString().startsWith("this")) {
			// System.out.println("getter method");
			this.publicFunctionalMethod--;
			accessormethodmap.replace(methodname, Boolean.TRUE);
		} else if (fieldmap.get(node.getExpression().toString()) == null) {
			// System.out.println("normal method");
		} else if (fieldmap.get(node.getExpression().toString())) {
			// System.out.println("*"+"getter method"+"*");
			this.publicFunctionalMethod--;
			accessormethodmap.replace(methodname, Boolean.TRUE);
		}
		return super.visit(node);
	}


	//NOPA Number of public attributes /Entity: Class
	//public修飾子のついたfield変数の数

	@Override
	public boolean visit(FieldDeclaration node) {
		System.out.print(node.toString());
		if (Modifier.isPublic(node.getModifiers())) {
			this.privatefields += 1;
			System.out.println("isPublic");
		}
		System.out.println(this.privatefields);
		return super.visit(node);
	}

	@Override
	public boolean visit(CompilationUnit node) {
		unit=node;
		return super.visit(node);
	}

}


