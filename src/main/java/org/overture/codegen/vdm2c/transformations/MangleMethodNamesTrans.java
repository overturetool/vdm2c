package org.overture.codegen.vdm2c.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.Vdm2cTag;
import org.overture.codegen.vdm2c.utils.NameMangler;

public class MangleMethodNamesTrans extends DepthFirstAnalysisCAdaptor
{

	public MangleMethodNamesTrans(TransAssistantIR transformationAssistant)
	{
	}

	@Override
	public void caseAMethodDeclIR(AMethodDeclIR node) throws AnalysisException
	{
		if (node.getTag() instanceof Vdm2cTag
				&& ((Vdm2cTag) node.getTag()).methodTags.contains(Vdm2cTag.MethodTag.Internal))
		{
			return;
		}
		node.setName(NameMangler.mangle(node));
	}

}
