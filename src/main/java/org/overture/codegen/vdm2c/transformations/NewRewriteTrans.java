package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.createIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.ANewExpIR;
import org.overture.codegen.ir.statements.ANewObjectDesignatorIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.utils.NameMangler;

public class NewRewriteTrans extends DepthFirstAnalysisCAdaptor
{
	public TransAssistantIR assist;

	public NewRewriteTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseANewExpIR(ANewExpIR node) throws AnalysisException
	{
		node.getArgs().add(0, createIdentifier("NULL", node.getSourceNode()));

		// FIXME this also need the call filtering on arguments
		for (SClassDeclIR cDef : assist.getInfo().getClasses())
		{
			if (!cDef.getName().equals(node.getName().getName()))
			{
				continue;
			}
			// this is the right class
			for (AMethodDeclIR method : cDef.getMethods())
			{
				if (!method.getIsConstructor()
						|| method.getFormalParams().size() != node.getArgs().size())
				{
					continue;
				}

				// this is one of the constructors it could be.
				// FIXME: add check to make sure it is the right one

				assist.replaceNodeWith(node, newApply(NameMangler.mangle(method), node.getArgs().toArray(new SExpIR[0])));
			}
		}

	}

	@Override
	public void caseANewObjectDesignatorIR(ANewObjectDesignatorIR node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseANewObjectDesignatorIR(node);
	}
}