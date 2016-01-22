package org.overture.codegen.vdm2c.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.vdm2c.extast.expressions.ACExpCG;

public class RemoveCWrappersTrans extends DepthFirstAnalysisCAdaptor
{
	public TransAssistantCG assist;

	final static String retPrefix = "ret_";

	public RemoveCWrappersTrans(TransAssistantCG assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseACExpCG(ACExpCG node) throws AnalysisException
	{
		assist.replaceNodeWith(node, node.getExp());
		node.getExp().apply(THIS);
	}
}