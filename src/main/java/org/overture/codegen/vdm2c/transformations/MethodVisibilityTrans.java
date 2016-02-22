package org.overture.codegen.vdm2c.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class MethodVisibilityTrans extends DepthFirstAnalysisCAdaptor
{

	public MethodVisibilityTrans(TransAssistantIR transformationAssistant)
	{
	}

	@Override
	public void caseAMethodDeclIR(AMethodDeclIR node) throws AnalysisException
	{
		if ("public".equals(node.getAccess()))
		{
			node.setStatic(false);
		} else
		{
			node.setStatic(true);
		}
	}
}
