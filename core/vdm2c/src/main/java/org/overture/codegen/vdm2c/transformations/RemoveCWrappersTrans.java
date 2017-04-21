package org.overture.codegen.vdm2c.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.expressions.ACExpIR;

public class RemoveCWrappersTrans extends DepthFirstAnalysisCAdaptor
{
	public TransAssistantIR assist;

	public RemoveCWrappersTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseACExpIR(ACExpIR node) throws AnalysisException
	{
		assist.replaceNodeWith(node, node.getExp());
		node.getExp().apply(THIS);
	}
}