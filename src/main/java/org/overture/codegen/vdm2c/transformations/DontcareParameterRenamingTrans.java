package org.overture.codegen.vdm2c.transformations;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.patterns.AIgnorePatternCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class DontcareParameterRenamingTrans extends DepthFirstAnalysisAdaptor
{
	public TransAssistantCG assist;

	final static String retPrefix = "ignore";

	public DontcareParameterRenamingTrans(TransAssistantCG assist)
	{
		this.assist = assist;
	}

	public void caseAMethodDeclCG(AMethodDeclCG node) throws AnalysisException
	{
		for (AFormalParamLocalParamCG formal : node.getFormalParams())
		{
			if (formal.getPattern() instanceof AIgnorePatternCG)
			{
				AIdentifierPatternCG id = new AIdentifierPatternCG();
				id.setName(assist.getInfo().getTempVarNameGen().nextVarName(retPrefix));
				id.setSourceNode(formal.getPattern().getSourceNode());
				formal.setPattern(id);
			}
		}
	}

}