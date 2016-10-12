package org.overture.codegen.vdm2c.distribution.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class DistTransTest extends DepthFirstAnalysisCAdaptor
{

	public DistTransTest(TransAssistantIR transformationAssistant)
	{
	}

//	@Override
//	public void caseAMethodDeclIR(AMethodDeclIR node) throws AnalysisException
//	{
//		System.out.println("Dist transformation, method name: " + node.getName());
//	}
//	
	@Override
	public void caseADefaultClassDeclIR(ADefaultClassDeclIR node)
			throws AnalysisException
	{
		
		//AClassHeaderDeclIR par = node.getAncestor(AClassHeaderDeclIR.class);
		//par.apply(this);
		//System.out.println("Class name: " + node.getName());
	}
	
//	@Override
//	public void caseAArrayDeclIR(AArrayDeclIR node) throws AnalysisException
//	{
//		// TODO Auto-generated method stub
//		super.caseAArrayDeclIR(node);
//		
//		System.out.println(node.getName());
//		
//		
//	}
	

}
