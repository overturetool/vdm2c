package org.overture.codegen.vdm2c.transformations;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.vdm2c.utils.NameConverter;

public class RenameValueFieldsTrans extends DepthFirstAnalysisAdaptor
{

	public RenameValueFieldsTrans(TransAssistantCG transformationAssistant)
	{
	}

	@Override
	public void caseAFieldDeclCG(AFieldDeclCG node) throws AnalysisException
	{
		node.setName(NameConverter.getCName(node));
		super.caseAFieldDeclCG(node);
	}

}
