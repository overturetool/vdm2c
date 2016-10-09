package org.overture.codegen.vdm2c.distribution.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class DistTransTest extends DepthFirstAnalysisCAdaptor
{

	public DistTransTest(TransAssistantIR transformationAssistant)
	{
	}

	@Override
	public void caseAMethodDeclIR(AMethodDeclIR node) throws AnalysisException
	{
		System.out.println("Dist transformation, method name: " + node.getName());
	}

}
