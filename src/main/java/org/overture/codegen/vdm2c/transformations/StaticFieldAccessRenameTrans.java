package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.isStaticFieldDefinition;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newIdentifier;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.utils.NameConverter;

public class StaticFieldAccessRenameTrans extends DepthFirstAnalysisCAdaptor
{
	public TransAssistantIR assist;

	public StaticFieldAccessRenameTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseAExplicitVarExpIR(AExplicitVarExpIR node)
			throws AnalysisException
	{
		if (isStaticFieldDefinition(node))
		{
			AIdentifierVarExpIR identifier = newIdentifier(NameConverter.getCFieldNameFromOriginal(node.getName()), node.getSourceNode());
			identifier.setType(node.getType());
			identifier.setIsLocal(false);
			assist.replaceNodeWith(node, identifier);
		}
		super.caseAExplicitVarExpIR(node);
	}

}
