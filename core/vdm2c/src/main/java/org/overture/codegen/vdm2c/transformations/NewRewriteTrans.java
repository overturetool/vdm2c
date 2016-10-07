package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.ANewExpIR;
import org.overture.codegen.ir.expressions.ANullExpIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.tags.CTags;
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
		node.getArgs().add(new ANullExpIR());

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

				AApplyExpIR constructorCall = newApply(NameMangler.mangle(method), node.getArgs().toArray(new SExpIR[0]));
				constructorCall.setTag(CTags.CONSTRUCTOR_CALL);
				assist.replaceNodeWith(node, constructorCall);
			}
		}

	}
}