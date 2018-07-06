package zemiA;

import java.util.Hashtable;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class MethodInformation{
	private MethodInvocation node;
	private int cm = 0;
	private int cc = 0;
	private Hashtable<String,ASTNode> classes = new Hashtable<String,ASTNode>();

	public MethodInformation(MethodInvocation m) {
		node = m;
	}

	void invocated(MethodInvocation m) {
		classes.put(m.getExpression().resolveTypeBinding().getName().toString(),m.getRoot());
		
		cm++;
	}

	public MethodInvocation getMethodInvocationNode() {
		return node;
	}

	public IMethodBinding getMethodBinding() {
		return node.resolveMethodBinding();
	}

	// CM: times of invocated by distinct method
	public int getCM() {
		return cm;
	}

	// CC: number of class which define CM method
	public int getCC() {
		cc = classes.size();
		return cc;
	}

}
