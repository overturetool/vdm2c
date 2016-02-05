package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.createIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;

import org.overture.codegen.ir.SExpCG;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AMethodDeclCG;
import org.overture.codegen.ir.declarations.SClassDeclCG;
import org.overture.codegen.ir.expressions.ANewExpCG;
import org.overture.codegen.ir.statements.ANewObjectDesignatorCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.vdm2c.utils.NameMangler;

public class NewRewriteTrans extends DepthFirstAnalysisAdaptor
{
	public TransAssistantCG assist;


	public NewRewriteTrans(TransAssistantCG assist)
	{
		this.assist = assist;
	}


	@Override
	public void caseANewExpCG(ANewExpCG node) throws AnalysisException
	{
		node.getArgs().add(0, createIdentifier("NULL", node.getSourceNode()));

		// FIXME this also need the call filtering on arguments
		for (SClassDeclCG cDef : assist.getInfo().getClasses())
		{
			if (!cDef.getName().equals(node.getName().getName()))
			{
				continue;
			}
			// this is the right class
			for (AMethodDeclCG method : cDef.getMethods())
			{
				if (!method.getIsConstructor()
						|| method.getFormalParams().size() != node.getArgs().size() )
				{
					continue;
				}

				// this is one of the constructors it could be.
				// FIXME: add check to make sure it is the right one

				assist.replaceNodeWith(node, newApply(NameMangler.mangle(method), node.getArgs().toArray(new SExpCG[0])));
			}
		}

	}

	@Override
	public void caseANewObjectDesignatorCG(ANewObjectDesignatorCG node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseANewObjectDesignatorCG(node);
	}
}