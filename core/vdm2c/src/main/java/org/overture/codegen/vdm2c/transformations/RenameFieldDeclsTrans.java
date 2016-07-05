package org.overture.codegen.vdm2c.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.utils.CTransUtil;
import org.overture.codegen.vdm2c.utils.NameConverter;

public class RenameFieldDeclsTrans extends DepthFirstAnalysisCAdaptor
{

	public RenameFieldDeclsTrans(TransAssistantIR transformationAssistant)
	{
	}

	@Override
	public void caseAFieldDeclIR(AFieldDeclIR node) throws AnalysisException
	{
		if (CTransUtil.isValueDefinition(node)
				|| CTransUtil.isStaticDefinition(node))
		{
			node.setName(NameConverter.getCName(node));
		}
		super.caseAFieldDeclIR(node);
	}

}
