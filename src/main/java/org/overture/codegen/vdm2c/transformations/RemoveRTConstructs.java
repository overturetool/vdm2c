package org.overture.codegen.vdm2c.transformations;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.statements.ACyclesStmCG;
import org.overture.codegen.cgast.statements.ADurationStmCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class RemoveRTConstructs extends DepthFirstAnalysisAdaptor
{
	public TransAssistantCG assist;

	public RemoveRTConstructs(TransAssistantCG assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseADurationStmCG(ADurationStmCG node) throws AnalysisException
	{
		assist.replaceNodeWith(node, node.getStm());
	}
	
	@Override
	public void caseACyclesStmCG(ACyclesStmCG node) throws AnalysisException
	{
		assist.replaceNodeWith(node, node.getStm());
	}
}