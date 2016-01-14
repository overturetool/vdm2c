package org.overture.codegen.cgen.transformations;

import static org.overture.codegen.cgen.transformations.CTransUtil.addArgument;
import static org.overture.codegen.cgen.transformations.CTransUtil.newTvpType;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class AddThisArgToMethodsTrans extends DepthFirstAnalysisAdaptor
{

	public AddThisArgToMethodsTrans(TransAssistantCG transformationAssistant)
	{
	}

	@Override
	public void caseAMethodDeclCG(AMethodDeclCG node) throws AnalysisException
	{
		if (!node.getIsConstructor())
		{
			addArgument("this", newTvpType(), 0, node.getFormalParams());
		}
	}

}
