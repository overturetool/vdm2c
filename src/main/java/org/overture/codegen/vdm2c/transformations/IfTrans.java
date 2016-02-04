package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.statements.AElseIfStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class IfTrans extends DepthFirstAnalysisAdaptor
{
	public TransAssistantCG assist;

	public IfTrans(TransAssistantCG assist)
	{
		this.assist = assist;
	}

	boolean mustUnpack(STypeCG type)
	{
		return type != null && !(type instanceof AExternalTypeCG);
	}

	void unpack(SExpCG exp)
	{
		assist.replaceNodeWith(exp, newApply("toBool", exp.clone()));
	}

	void process(SExpCG exp)
	{
		if (mustUnpack(exp.getType()))
		{
			unpack(exp);
		}
	}

	@Override
	public void caseAIfStmCG(AIfStmCG node) throws AnalysisException
	{
		super.caseAIfStmCG(node);
		process(node.getIfExp());

		for (AElseIfStmCG elseIf : node.getElseIf())
		{
			process(elseIf.getElseIf());
		}
	}
}