package org.overture.codegen.vdm2c.transformations;

import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.utils.NameMangler;

public class MangleMethodNamesTrans extends DepthFirstAnalysisAdaptor
{

	public MangleMethodNamesTrans(TransAssistantIR transformationAssistant)
	{
	}

	@Override
	public void caseAMethodDeclIR(AMethodDeclIR node) throws AnalysisException
	{
		node.setName(NameMangler.mangle(node));
	}

}
