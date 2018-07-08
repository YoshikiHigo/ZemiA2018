package zemiA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class MethodInformation{
	private String methodName;
	private IMethodBinding methodBind;
	private int cm = 0;
	private int cc = 0;
	private List<IMethodBinding> invokingMethods = new ArrayList<IMethodBinding>();

	public static final int FOR_DISPLAY = 0;
	public static final int FOR_REFACTORING = 1;

	public MethodInformation(IMethodBinding declaratedMethodBind) {
		methodBind = declaratedMethodBind;
		methodName = methodBind.getName().toString();
	}

	// Setter
	public void invocated(IMethodBinding invokingMethodBind) {
		invokingMethods.add(invokingMethodBind);
		cm++;
	}

	// CM: times of invocated by distinct method
	public int getCM() {
		return cm;
	}

	// CC: number of class which define CM method
	public int getCC() {
		HashMap<ITypeBinding,Integer> invokingClassesList = new HashMap<ITypeBinding,Integer>();
		for(IMethodBinding invokingMethodBind: invokingMethods) {
			Integer i = invokingClassesList.get(invokingMethodBind.getDeclaringClass());
			i = i==null? 0 : i;  //if i==null: first invocation
			invokingClassesList.put(invokingMethodBind.getDeclaringClass(),++i);
		}
		cc = invokingClassesList.size();
		return cc;
	}

	public boolean isShotgunSurgery() {
		// CM > ShortMemoryCapacity 7
		// CC > MANY 4
		return getCM()>7 && getCC()>4;
	}

	public IMethodBinding getMethodBinding() {
		return methodBind;
	}

	public void printMethodInfomation(int mode){
		if(mode == FOR_DISPLAY || mode == FOR_REFACTORING) {
			System.out.println(methodBind.getDeclaringClass().getName().toString() +": "+ methodName);
			System.out.println("CM: " + getCM());
			System.out.println("CC: " + getCC());
		}

		if(mode == FOR_REFACTORING) {
			// for refactoring
			for(IMethodBinding method: invokingMethods) {
				System.out.println(method.getDeclaringClass().getName().toString() +":"+ method.getName().toString());
			}
		}
		System.out.println("");
	}

	public static MethodInformation getMethodInformation(IMethodBinding methodBind, List<MethodInformation> methods) {
		for(MethodInformation methodInformation: methods) {
			if(methodInformation.getMethodBinding().equals(methodBind)) {
				return methodInformation;
			}
		}
		// if the method is not project method
		return null;
	}
}