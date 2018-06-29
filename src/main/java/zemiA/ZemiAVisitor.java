package zemiA;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class ZemiAVisitor extends ASTVisitor {
	CompilationUnit unit;
	public ZemiAVisitor(CompilationUnit compilationUnit) {
		this.unit = compilationUnit;
	}

	@Override
	public boolean visit(SimpleName node) {
		//System.out.println(node.getIdentifier());
		return super.visit(node);
	}

  @Override
  public boolean visit(MethodDeclaration node) {
    System.out.print(node.getName() + " method LOC = ");
    System.out.println(unit.getLineNumber(node.getStartPosition() + node.getLength() -1)+1-unit.getLineNumber(node.getStartPosition()));
    return super.visit(node);
  }

  @Override
  public boolean visit(PrimitiveType node) {
    // TODO Auto-generated method stub
    return super.visit(node);
  }

	
	
}
