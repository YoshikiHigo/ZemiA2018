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
	private List<String> parameters = new ArrayList<String>();
	private int methodatfddirect=0;
	private int methodatfdvaimethod=0;
	private int methodatfd=0;
	private int noav=0;//the Number of accessed variables
	private int fdp=0;//foreign data provider
	private double laa=0;
	private List<IMethodBinding> invokingmethod = new ArrayList<IMethodBinding>();
	private List<String> accessedfeildlist = new ArrayList<String>();
	private List<String> accessednormalvariables = new ArrayList<String>();
	private List<ITypeBinding> providerclasses = new ArrayList<ITypeBinding>();
	private ITypeBinding declaringclass;
	private int fromownclass=0;
	private int fromforeignclass=0;

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
		this.accessor = b;
	}

	public boolean isAccessor() {
		return this.accessor;
	}

	public void setParameter(String s) {
		this.parameters.add(s);
	}

	public List<String> getParameter(){
		return this.parameters;
	}

	public void setMethodATFDDirect(int argmethodatfd) {
		this.methodatfddirect=argmethodatfd;
	}

	public int getMethodATFDDirect() {
		return this.methodatfddirect;
	}

	public void setMethodATFDViaMethod(int argmethodatfd) {
		this.methodatfdvaimethod=argmethodatfd;
	}

	public int getMethodATFDViaMethod() {
		return this.methodatfdvaimethod;
	}

	public void setInvokingMethods(IMethodBinding mb) {
		this.invokingmethod.add(mb);
	}

	public List<IMethodBinding> getInvokingMethods() {
		return this.invokingmethod;
	}

	public void setAccessedFields(String s) {
		this.accessedfeildlist.add(s);
	}

	public List<String> getAccessedFields() {
		return this.accessedfeildlist;
	}

	public void setNormalVariableList(String s) {
		this.accessednormalvariables.add(s);
	}

	public List<String> getNormalVariableList() {
		return this.accessednormalvariables;
	}

	public void setNOAV() {
		this.noav=this.accessedfeildlist.size()+this.accessednormalvariables.size()+this.parameters.size();
	}

	public int getNOAV() {
		return this.noav;
	}

	public void setmethodATFD() {
		this.methodatfd=this.methodatfddirect+this.methodatfdvaimethod;
	}

	public int getmethodATFD() {
		return this.methodatfd;
	}

	public boolean isAccessed(String s) {
		return this.accessedfeildlist.contains(s);
	}

	public boolean isInvoked(IMethodBinding mb) {
		return this.invokingmethod.contains(mb);
	}

	public void setProviderClasses(ITypeBinding tp) {
		if(this.declaringclass.equals(tp)) {
			this.fromownclass+=1;
		}else if(this.providerclasses.contains(tp)) {
			this.fromforeignclass+=1;
		}else {
			this.providerclasses.add(tp);
			this.fromforeignclass+=1;
		}
	}

	public List<ITypeBinding> getProviderClasses(){
		return this.providerclasses;
	}

	public void setFDP() {
		this.fdp = this.providerclasses.size();
	}

	public int getFDP() {
		return this.fdp;
	}

	public void setDeclaringClass(ITypeBinding tb) {
		this.declaringclass=tb;
	}

	public ITypeBinding getDeclaringClass() {
		return this.declaringclass;
	}

	public void setLAA() {
		this.laa=(double)this.fromownclass/(this.fromownclass+this.fromforeignclass);
	}

	public double getLAA() {
		return this.laa;
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