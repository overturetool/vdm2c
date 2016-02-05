package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.statements.AElseIfStmIR;
import org.overture.codegen.ir.statements.AIfStmIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class IfTrans extends DepthFirstAnalysisAdaptor
{
	public TransAssistantIR assist;

	public IfTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	boolean mustUnpack(STypeIR type)
	{
		return type != null && !(type instanceof AExternalTypeIR);
	}

	void unpack(SExpIR exp)
	{
		assist.replaceNodeWith(exp, newApply("toBool", exp.clone()));
	}

	void process(SExpIR exp)
	{
		if (mustUnpack(exp.getType()))
		{
			unpack(exp);
		}
	}

	@Override
	public void caseAIfStmIR(AIfStmIR node) throws AnalysisException
	{
		super.caseAIfStmIR(node);
		process(node.getIfExp());

		for (AElseIfStmIR elseIf : node.getElseIf())
		{
			process(elseIf.getElseIf());
		}
	}
}