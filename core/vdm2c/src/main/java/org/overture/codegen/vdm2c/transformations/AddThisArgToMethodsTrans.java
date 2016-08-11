package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.addArgument;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newExternalType;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newTvpType;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.AVoidTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.Vdm2cTag;

public class AddThisArgToMethodsTrans extends DepthFirstAnalysisCAdaptor
{

	public AddThisArgToMethodsTrans(TransAssistantIR transformationAssistant)
	{
	}

	@Override
	public void caseAMethodDeclIR(AMethodDeclIR node) throws AnalysisException
	{
		if (!node.getIsConstructor())
		{
			if (node.getTag() instanceof Vdm2cTag
					&& ((Vdm2cTag) node.getTag()).methodTags.contains(Vdm2cTag.MethodTag.Internal))
			{
				return;
			}

			if (!(node.getMethodType().getResult() instanceof AVoidTypeIR))
			{
				AMethodTypeIR oldType = node.getMethodType().clone();
				node.getMethodType().setResult(newTvpType());
				node.getMethodType().setSourceNode(oldType.getSourceNode());
				node.getMethodType().getResult().setSourceNode(oldType.getResult().getSourceNode());
			}
			SClassDeclIR cDef = node.getAncestor(SClassDeclIR.class);
			addArgument("this", newExternalType(cDef.getName() + "CLASS"), 0, node.getFormalParams());
		}
	}

}
