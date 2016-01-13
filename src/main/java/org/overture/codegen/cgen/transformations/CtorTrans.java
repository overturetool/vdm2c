package org.overture.codegen.cgen.transformations;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.statements.ANewObjectDesignatorCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class CtorTrans extends DepthFirstAnalysisAdaptor
{
	public TransAssistantCG assist;

	final static String retPrefix = "ctor_";

	public CtorTrans(TransAssistantCG assist)
	{
		this.assist = assist;
	}

	public void caseAMethodDeclCG(AMethodDeclCG node) throws AnalysisException
	{
		if (node.getIsConstructor())
		{
			node.setName(node.getName() + "_init");
			node.getMethodType().setResult(new AVoidTypeCG());
		}
	}

	@Override
	public void caseANewExpCG(ANewExpCG node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseANewExpCG(node);
	}

	@Override
	public void caseANewObjectDesignatorCG(ANewObjectDesignatorCG node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseANewObjectDesignatorCG(node);
	}
}