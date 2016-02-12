package org.overture.codegen.vdm2c.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.utils.CTransUtil;
import org.overture.codegen.vdm2c.utils.NameConverter;

public class RenameValueFieldsTrans extends DepthFirstAnalysisCAdaptor
{

	public RenameValueFieldsTrans(TransAssistantIR transformationAssistant)
	{
	}

	@Override
	public void caseAFieldDeclIR(AFieldDeclIR node) throws AnalysisException
	{
		if (CTransUtil.isValueDefinition(node))
		{
			node.setName(NameConverter.getCName(node));
		}
		super.caseAFieldDeclIR(node);
	}

}
