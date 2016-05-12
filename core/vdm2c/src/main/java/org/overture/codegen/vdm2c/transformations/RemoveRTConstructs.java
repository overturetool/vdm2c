package org.overture.codegen.vdm2c.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.statements.ACyclesStmIR;
import org.overture.codegen.ir.statements.ADurationStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class RemoveRTConstructs extends DepthFirstAnalysisCAdaptor
{
	public TransAssistantIR assist;

	public RemoveRTConstructs(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseADurationStmIR(ADurationStmIR node)
			throws AnalysisException
	{
		assist.replaceNodeWith(node, node.getStm());
	}

	@Override
	public void caseACyclesStmIR(ACyclesStmIR node) throws AnalysisException
	{
		assist.replaceNodeWith(node, node.getStm());
	}
}
