package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.addArgument;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newExternalType;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newTvpType;

import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AMethodDeclCG;
import org.overture.codegen.ir.declarations.SClassDeclCG;
import org.overture.codegen.ir.types.AVoidTypeCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.vdm2c.Vdm2cTag;

public class AddThisArgToMethodsTrans extends DepthFirstAnalysisAdaptor
{

	public AddThisArgToMethodsTrans(TransAssistantCG transformationAssistant)
	{
	}

	@Override
	public void caseAMethodDeclCG(AMethodDeclCG node) throws AnalysisException
	{
		if (!node.getIsConstructor())
		{
			if (node.getTag() instanceof Vdm2cTag
					&& ((Vdm2cTag) node.getTag()).methodTags.contains(Vdm2cTag.MethodTag.Internal))
			{
				return;
			}

			if (!(node.getMethodType().getResult() instanceof AVoidTypeCG))
			{
				node.getMethodType().setResult(newTvpType());
			}
			SClassDeclCG cDef = node.getAncestor(SClassDeclCG.class);
			addArgument("this", newExternalType(cDef.getName() + "CLASS"), 0, node.getFormalParams());
		}
	}

}
