package zemiA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class MethodInformation{
	private IMethodBinding methodBind;
	private int cm = 0;
	private int cc = 0;
	private int methodLOC = 0;
	private List<ITypeBinding> invokingClasses = new ArrayList<ITypeBinding>();

	private boolean accessor = false;

	public MethodInformation(IMethodBinding declaratedMethodBind) {
		methodBind = declaratedMethodBind;
	}

	// setter
	public void invocated(IMethodBinding invokingMethodBind) {
		ITypeBinding defineClass = invokingMethodBind.getDeclaringClass();
		invokingClasses.add(defineClass);
		cm++;
	}

	public void setMethodLOC(int loc) {
		methodLOC = loc;
	}

	public void setAccessor(boolean b) {
		accessor = b;
	}

	// CM: times of invocated by distinct method
	public int getCM() {
		return cm;
	}

	// CC: number of class which define CM method
	public int getCC() {
		HashMap<String,ITypeBinding> classes = new HashMap<String,ITypeBinding>();
		for(ITypeBinding invokingclass: invokingClasses) {
			classes.put(invokingclass.getName().toString(),invokingclass);
		}
		cc = classes.size();
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

}