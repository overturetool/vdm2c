package org.overture.codegen.cgen.transformations;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgen.utils.NameMangler;
import org.overture.codegen.trans.assistants.TransAssistantCG;

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
