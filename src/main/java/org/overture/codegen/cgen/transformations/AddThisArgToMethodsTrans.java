package org.overture.codegen.cgen.transformations;

import static org.overture.codegen.cgen.transformations.CTransUtil.addArgument;
import static org.overture.codegen.cgen.transformations.CTransUtil.newExternalType;
import static org.overture.codegen.cgen.transformations.CTransUtil.newTvpType;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.SClassDeclCG;
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
			node.getMethodType().setResult(newTvpType());
			SClassDeclCG cDef = node.getAncestor(SClassDeclCG.class);
			addArgument("this", newExternalType(cDef.getName() + "CLASS"), 0, node.getFormalParams());
		}
	}

}
