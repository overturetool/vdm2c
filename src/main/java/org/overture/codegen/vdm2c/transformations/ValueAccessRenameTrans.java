package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.isValueDefinition;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.utils.GlobalFieldUtil;

public class ValueAccessRenameTrans extends DepthFirstAnalysisCAdaptor
{
	public TransAssistantIR assist;
	final GlobalFieldUtil fieldUtil;

	public ValueAccessRenameTrans(TransAssistantIR assist)
	{
		this.assist = assist;
		this.fieldUtil = new GlobalFieldUtil(assist);
	}

	@Override
	public void caseAExplicitVarExpIR(AExplicitVarExpIR node)
			throws AnalysisException
	{
		if (isValueDefinition(node))
		{
			fieldUtil.replace(node);
		}
		super.caseAExplicitVarExpIR(node);
	}

}
