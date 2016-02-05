package org.overture.codegen.vdm2c.transformations;

import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.utils.NameConverter;

public class RenameValueFieldsTrans extends DepthFirstAnalysisAdaptor
{

	public RenameValueFieldsTrans(TransAssistantIR transformationAssistant)
	{
	}

	@Override
	public void caseAFieldDeclIR(AFieldDeclIR node) throws AnalysisException
	{
		node.setName(NameConverter.getCName(node));
		super.caseAFieldDeclIR(node);
	}

}
