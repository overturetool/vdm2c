package org.overture.codegen.vdm2c.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.patterns.AIdentifierPatternCG;
import org.overture.codegen.ir.patterns.AIgnorePatternCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class IgnoreRenamingTrans extends DepthFirstAnalysisCAdaptor
{
	public TransAssistantCG assist;

	final static String retPrefix = "ignore";

	public IgnoreRenamingTrans(TransAssistantCG assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseAIgnorePatternCG(AIgnorePatternCG node)
			throws AnalysisException
	{
		AIdentifierPatternCG id = new AIdentifierPatternCG();
		id.setName(assist.getInfo().getTempVarNameGen().nextVarName(retPrefix));
		id.setSourceNode(node.getSourceNode());
		assist.replaceNodeWith(node, id);
	}

}