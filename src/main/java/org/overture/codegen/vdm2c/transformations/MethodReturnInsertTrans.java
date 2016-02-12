package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newReturnStm;
import static org.overture.codegen.vdm2c.utils.CTransUtil.toExp;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.types.AVoidTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.Vdm2cTag;

public class MethodReturnInsertTrans extends DepthFirstAnalysisCAdaptor
{
	public TransAssistantIR assist;

	public MethodReturnInsertTrans(TransAssistantIR assist)
	{
		this.assist = assist;
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
				// it returns something so insert the return if missing
				insertReturn(node.getBody());
			}

		}
	}

	private void insertReturn(SStmIR body) throws AnalysisException
	{
		if (body instanceof AReturnStmIR)
		{
			return;
		} else if (body instanceof ABlockStmIR)
		{
			ABlockStmIR block = (ABlockStmIR) body;
			if (block.getStatements().isEmpty())
			{
				throw new AnalysisException("internal error nothing to return");
			} else
			{
				insertReturn(((ABlockStmIR) body).getStatements().getLast());
			}

		} else
		{
			assist.replaceNodeWith(body, newReturnStm(toExp(body.clone())));
		}
	}

}
