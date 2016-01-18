package org.overture.codegen.vdm2c.transformations;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.vdm2c.utils.NameMangler;

public class MangleMethodNamesTrans extends DepthFirstAnalysisAdaptor
{

	public MangleMethodNamesTrans(TransAssistantCG transformationAssistant)
	{
	}

	@Override
	public void caseAMethodDeclCG(AMethodDeclCG node) throws AnalysisException
	{
		node.setName(NameMangler.mangle(node));
	}

}
