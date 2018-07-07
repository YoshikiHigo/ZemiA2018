package zemiA;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ZemiAListMakeVisitor extends ASTVisitor{
	private List<IMethodBinding> methods = new ArrayList<IMethodBinding>();
	private List<IMethodBinding> allMethods;

	@Override
	public boolean visit(TypeDeclaration node) {
		for(MethodDeclaration mb: node.getMethods()) {
			methods.add(mb.resolveBinding());
		}
		allMethods.addAll(methods);
		return super.visit(node);
	}

	public List<IMethodBinding> getAllMethodsList(){
		return allMethods;
	}
}
