package org.overture.codegen.vdm2c.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.patterns.AIgnorePatternIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class IgnoreRenamingTrans extends DepthFirstAnalysisCAdaptor
{
	public TransAssistantIR assist;

	final static String retPrefix = "ignore";

	public IgnoreRenamingTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseAIgnorePatternIR(AIgnorePatternIR node)
			throws AnalysisException
	{
		AIdentifierPatternIR id = new AIdentifierPatternIR();
		id.setName(assist.getInfo().getTempVarNameGen().nextVarName(retPrefix));
		id.setSourceNode(node.getSourceNode());
		assist.replaceNodeWith(node, id);
	}

}