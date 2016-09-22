package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.isStaticFieldDefinition;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.utils.GlobalFieldUtil;

public class StaticFieldAccessRenameTrans extends DepthFirstAnalysisCAdaptor
{
	public TransAssistantIR assist;
	final GlobalFieldUtil fieldUtil;

	public StaticFieldAccessRenameTrans(TransAssistantIR assist)
	{
		this.assist = assist;
		this.fieldUtil = new GlobalFieldUtil(assist);
	}

	@Override
	public void caseAExplicitVarExpIR(AExplicitVarExpIR node)
			throws AnalysisException
	{
		super.caseAExplicitVarExpIR(node);
		if (isStaticFieldDefinition(node, assist.getInfo()))
		{
			fieldUtil.replaceWithIdentifier(node);
		}
	}

}
