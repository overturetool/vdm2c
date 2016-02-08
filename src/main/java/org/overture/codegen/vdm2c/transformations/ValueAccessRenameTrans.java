package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.isValueDefinition;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newIdentifier;

import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.utils.NameConverter;

public class ValueAccessRenameTrans extends DepthFirstAnalysisAdaptor
{
	public TransAssistantIR assist;

	public ValueAccessRenameTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseAExplicitVarExpIR(AExplicitVarExpIR node)
			throws AnalysisException
	{
		if (isValueDefinition(node))
		{
			AIdentifierVarExpIR identifier = newIdentifier(NameConverter.getCFieldNameFromOriginal(node.getName()), node.getSourceNode());
			identifier.setIsLocal(false);
			assist.replaceNodeWith(node, identifier);
		}
		super.caseAExplicitVarExpIR(node);
	}

}
