package zemiA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
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

	private List<ClassInformation> hierarchyTop = new ArrayList<ClassInformation>();

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

	/* =========== */
	private int publicfields;

	CompilationUnit unit;
	private IMethodBinding methodname;
	private ITypeBinding classname;
	private HashMap<String, Boolean> fieldmap = new HashMap<String, Boolean>();
	private ArrayList<String> fieldlist = new ArrayList<String>();
	private ArrayList<String> methodlist = new ArrayList<String>();
	//	private Map<String, Boolean> accessormethodmap = new HashMap<String, Boolean>();
	private HashMap<String, Integer> methodLOCmap = new HashMap<String, Integer>();
	private int publicFunctionalMethod;
	private int publicMethod;
	private int woc;
	private int classLOC;
	private int noam = 0;
	//	public int dummy;//NOPA test
	private int classATFDdirect=0;
	private int classATFDviaaccessor=0;
	private int methodATFDdirect=0;
	private int methodATFDviaaccessor=0;
	private int foreigndata=0;
	private int laalocal=0;
	/* ================= */




	@Override
	public void endVisit(CompilationUnit node) {
		// Judgement over Project by Lists

		//chapter 5
		for (MethodInformation methodInformation : allDeclaratedMethods) {
			for(IMethodBinding calledmethod : methodInformation.getInvokingMethods()) {
				MethodInformation invokedmethodinfo = getMethodInformation(calledmethod);
				if(invokedmethodinfo!=null && invokedmethodinfo.isAccessor()) {
					this.methodATFDviaaccessor+=1;
					//以下、FDPを求めるための準備 : via accessor method
					for(ClassInformation classinformation : allDeclaratedClasses) {
						if(classinformation.isDefined(calledmethod)) {
							this.foreigndata+=1;
							ITypeBinding tb = classinformation.getClassBinding();
							methodInformation.setProviderClasses(tb);
						}
					}
				}
				List<String> accessedfield = methodInformation.getAccessedFields();
				List<ITypeBinding> provider = methodInformation.getProviderClasses();
				List<String> declaringfield;
				for(ClassInformation classinformation : allDeclaratedClasses) {
					declaringfield=classinformation.getDeclaringFieldList();
					for(String field:accessedfield) {
						//						if(declaringfield.contains(field)) {
						if(classinformation.isDefined(field)) {
							//							if(!provider.contains(classinformation.getClassBinding())) {
							methodInformation.setProviderClasses(classinformation.getClassBinding());
							//							}
						}
					}
				}
			}
			methodInformation.setFDP();
			methodInformation.setLAA();
			methodInformation.setMethodATFDViaMethod(this.methodATFDviaaccessor);
			methodInformation.setmethodATFD();
			this.methodATFDviaaccessor=0;
		}
		for(ClassInformation classinformation : allDeclaratedClasses) {
			List<String> classaccessedfields = new ArrayList<String>();
			for(IMethodBinding declaredmethod : classinformation.getMethodsList()) {
				MethodInformation method = getMethodInformation(declaredmethod);
				for(String s : method.getAccessedFields()) {
					if(!classaccessedfields.contains(s)) {
						classaccessedfields.add(s);
					}
				}
			}
			for(String accessedfield:classaccessedfields) {
				if(!classinformation.isDefined(accessedfield)) {
					this.classATFDdirect+=1;
				}
			}
			List<IMethodBinding> classinvokedmethodlist = new ArrayList<IMethodBinding>();
			for(IMethodBinding declaredmethod : classinformation.getMethodsList()) {
				MethodInformation method = getMethodInformation(declaredmethod);
				for(IMethodBinding mb : method.getInvokingMethods()) {
					if(!classinvokedmethodlist.contains(mb)) {
						classinvokedmethodlist.add(mb);
					}
				}
			}
			for(IMethodBinding mb : classinvokedmethodlist) {
				MethodInformation method = getMethodInformation(mb);
				if(method!=null) {
					if(method.isAccessor()) {
						this.classATFDviaaccessor+=1;
					}
				}
			}
			classinformation.setClassATFD(this.classATFDdirect+this.classATFDviaaccessor);
			this.classATFDviaaccessor=0;
			this.classATFDdirect=0;
		}
		for(ClassInformation classinformation : allDeclaratedClasses) {
			List<String> classfields = classinformation.getDeclaringFieldList();
			List<IMethodBinding> classmethods = classinformation.getMethodsList();
			int nofield = classfields.size();
			int length=classmethods.size();
			boolean[][] relation = new boolean[nofield][length];
			for(int i = 0;i<nofield;i++) {
				int noftrueprev=0;
				for(int j = 0;j<length;j++) {
					relation[i][j]=getMethodInformation(classmethods.get(j)).isAccessed(classfields.get(i));
					if(relation[i][j]) {
						noftrueprev+=1;
					}
				}
				boolean conti=true;
				do {
					int noftrue=0;
					for(int j = 0;j<length;j++) {
						if(relation[i][j]) {
							for(int k=0;k<length;k++) {
								boolean temp = getMethodInformation(classmethods.get(j)).isInvoked(classmethods.get(k));
								relation[i][k]=relation[i][k]||temp;
							}
						}
					}
					for(int j = 0;j<length;j++) {
						if(relation[i][j]) {
							noftrue+=1;
						}
					}
					if(noftrue==noftrueprev) {
						conti=false;
					}else {
						conti=true;
						noftrueprev=noftrue;
						noftrue=0;
					}
				}while(conti);
			}
			int pair=0;
			boolean makepair=false;
			for(int i = 0;i<length;i++) {
				for(int j = i;j<length;j++) {
					if(i!=j) {
						for(int k = 0;k<nofield;k++) {
							makepair = makepair || (relation[k][i] && relation[k][j]);
						}
						if(makepair) {
							pair+=1;
						}
					}else {

					}
				}
			}
			int maxpair = length*(length-1)/2;
			double tcc = (double) pair/maxpair;
			classinformation.setTCC(tcc);
		}

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
			}else {
				hierarchyTop.add(pData);
			}
		}
		//end Judgement

		// Set Classes Information
		for(ClassInformation classInformation: allDeclaratedClasses) {
			classInformation.setClassMethods(allDeclaratedMethods);
		}


		// Print Classes Information
		System.out.println("print class informations: ");
		for(ClassInformation classInformation: allDeclaratedClasses) {
			classInformation.printClassInformation();
//			for(MethodInformation methodInformation: classInformation.getDisharmonyMethods()) {
//				System.out.println(methodInformation.getName() + methodInformation.numOfDisharmony());
//			}
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


		publicFunctionalMethod = 0;
		publicMethod = 0;
		//classMethods = new ArrayList<IMethodBinding>();
		allDeclaratedClasses.add(new ClassInformation(node.resolveBinding()));

		classLOC = unit.getLineNumber(node.getStartPosition() + node.getLength() - 1) + 1
				- unit.getLineNumber(node.getStartPosition());
		//		for(FieldDeclaration vb: node.getFields()) {
		//			System.out.println(vb.toString());
		//			System.out.println(vb.fragments().get(0).toString());
		//		}
		//		System.out.println("*");
		//			System.out.println(node.resolveBinding().getDeclaringMember());
		classname = node.resolveBinding();
		return super.visit(node);
	}

	@Override
	public void endVisit(TypeDeclaration node) {
		// set searched class information
		//ClassInformation checkedClass = new ClassInformation(node.resolveBinding());
		ClassInformation checkedClass = getClassInformation(node.resolveBinding());
		//allDeclaratedClasses.add(checkedClass);

		checkedClass.setClassInformation(classMethods ,classMaxNesting , NOM, WMC, NProtM,
				pmlist, ismlist, alist, isalist, usedSuperFields, usedSuperMethods);

		ITypeBinding stb = node.resolveBinding().getSuperclass();
		if(stb != null) {
			checkedClass.setParent(stb);
		}

		if (this.publicMethod != 0) {
			woc = this.publicFunctionalMethod / this.publicMethod;
		}
		for(String str : fieldlist) {
			checkedClass.setDeclaringFieldList(str);
		}
		fieldlist.clear();
		checkedClass.setClassWOC(woc);
		checkedClass.setClassLOC(classLOC);
		checkedClass.setNOAM(this.noam);
		checkedClass.setNOPA(this.publicfields);
		this.noam=0;
		this.publicfields=0;

		super.endVisit(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		// to access from MethodInvocation to MethodDeclaration
		methodDeclarationStack.push(node);

		/* =============== */
		this.publicMethod++;
		this.publicFunctionalMethod++;
		/*System.out.print(node.getName() + " method LOC = ");
		System.out.println(unit.getLineNumber(node.getStartPosition() + node.getLength() - 1) + 1
				- unit.getLineNumber(node.getStartPosition()));*/
		MethodInformation aaa = getMethodInformation(node.resolveBinding());
		aaa.setMethodLOC(unit.getLineNumber(node.getStartPosition() + node.getLength() - 1) + 1
				- unit.getLineNumber(node.getStartPosition()));
		methodname = node.resolveBinding();
		//		accessormethodmap.put(methodname, Boolean.FALSE);
		//		methodlist.add(methodname);
		int methodloc = unit.getLineNumber(node.getStartPosition() + node.getLength() - 1) + 1
				- unit.getLineNumber(node.getStartPosition());
		//		methodLOCmap.put(methodname, methodloc);
		for(Object para:node.parameters()) {
			//			System.out.println(node.parameters());
			//			System.out.println(para.toString());
			aaa.setParameter(para.toString());
		}
		/* ============== */

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

		MethodInformation aaa = getMethodInformation(methodname);
		aaa.setDeclaringClass(classname);
		aaa.setNOAV();
		List<String> accessedfieldlist = aaa.getAccessedFields();
		for(String s:accessedfieldlist) {
			if(!fieldlist.contains(s)) {
				this.methodATFDdirect+=1;
			}
		}
		aaa.setMethodATFDDirect(this.methodATFDdirect);
		this.methodATFDdirect=0;

		getMethodInformation(node.resolveBinding()).setMaxNesting(methodMaxNesting);
		methodMaxNesting = 0;
		methodNesting = 0;
		super.endVisit(node);
	}

	@Override
	public boolean visit(MethodInvocation node) {
		allInvokedMethods.add(node);
		allInvokingMethods.put(node, methodDeclarationStack.peek());

		MethodInformation aaa = getMethodInformation(methodname);
		List<IMethodBinding> invokingmethod = aaa.getInvokingMethods();
		if(!invokingmethod.contains(node.resolveMethodBinding())) {
			aaa.setInvokingMethods(node.resolveMethodBinding());
		}

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
	public boolean visit(ReturnStatement node) {
		//		 System.out.println(node.getExpression());
		MethodInformation m = this.getMethodInformation(methodname);
		if (node.getExpression() == null) {

		} else if (node.getExpression().toString().startsWith("this")) {
			//			 System.out.println("<<getter method>>");
			this.publicFunctionalMethod--;
			//			accessormethodmap.replace(methodname, Boolean.TRUE);
			m.setAccessor(true);
			noam+=1;
		} else if (fieldlist.contains(node.getExpression().toString())) {
			//			 System.out.println("*"+"<<getter method>>"+"*");
			this.publicFunctionalMethod--;
			//			accessormethodmap.replace(methodname, Boolean.TRUE);
			m.setAccessor(true);
			noam+=1;
		}
		return super.visit(node);
	}

	// NOPA Number of public attributes /Entity: Class
	// public修飾子のついたfield変数の数

	@Override
	public boolean visit(FieldDeclaration node) {
		// TODO Auto-generated method stub
		//		System.out.println(node.fragments());
		//		System.out.println(node.getType());
		if (Modifier.isPublic(node.getModifiers())) {
			this.publicfields += 1;
			//System.out.println("isPublic");
		}
		//		fieldlist.add(node.toString());
		//		System.out.println(this.publicfields);

		if((Modifier.PUBLIC & node.getModifiers()) != 0) {
			isalist.add(node);
		}else if((Modifier.PROTECTED & node.getModifiers()) != 0){
			isalist.add(node);
			NProtM++;
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(CompilationUnit node) {
		unit = node;
		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		//			System.out.println("*"+node.getName());
		if(node.resolveBinding().isField()) {
			//				System.out.println("*"+node.getName());
			fieldlist.add(node.getName().toString());
		}
		return super.visit(node);
	}


	@Override
	public boolean visit(Assignment node) {
		//			System.out.println("*"+node.getIdentifier()+"*");
		//		System.out.println("*"+node.getLeftHandSide()+node.getOperator()+node.getRightHandSide());
		String lefthand = node.getLeftHandSide().toString();
		String righthand = node.getRightHandSide().toString();
		MethodInformation aaa = getMethodInformation(methodname);
		List<String> parameters = aaa.getParameter();
		//? Setter method
		if(fieldlist.contains(lefthand)) {
			for(String para:parameters) {
				if(para.contains(righthand)) {
					//					System.out.println(methodname);
					aaa.setAccessor(true);
					noam+=1;
				}
			}
		}
		//accessed variables list for NOAV
		//? is field
		List<String> accessedfieldlist = aaa.getAccessedFields();
		List<String> accessednormalvariablelist = aaa.getNormalVariableList();
		if(fieldlist.contains(lefthand)) {//lefthand is a field variable
			if(!accessedfieldlist.contains(lefthand)) {
				aaa.setAccessedFields(lefthand);
			}
		}else {
			for(String para:parameters) {//lefthand is a parameter or a normal variable
				if(!para.contains(lefthand)) {
					if(!accessednormalvariablelist.contains(lefthand)) {
						aaa.setNormalVariableList(lefthand);
					}
				}
			}
		}
		if(fieldlist.contains(righthand)) {
			if(!accessedfieldlist.contains(righthand)) {
				aaa.setAccessedFields(righthand);
			}
		}else {
			for(String para:parameters) {
				if(!para.contains(righthand)) {
					if(!accessednormalvariablelist.contains(righthand)) {
						aaa.setNormalVariableList(righthand);
					}
				}

			}
		}

		return super.visit(node);
	}

	@Override
	public boolean visit(FieldAccess node) {
		MethodInformation aaa = getMethodInformation(methodname);
		List<String> bbb = aaa.getAccessedFields();
		if(!(bbb.contains(node.getName().toString()))) {
			//			System.out.println(node.getName());
			aaa.setAccessedFields(node.getName().toString());
		}
		//		System.out.println(methodname);
		//		System.out.println(bbb);
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

	public List<ClassInformation> getClassInformation(){
		return allDeclaratedClasses;
	}

	public List<ClassInformation> getHierarchyTop(){
		return hierarchyTop;
	}

}

