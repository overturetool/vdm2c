package org.overture.codegen.vdm2c.transformations;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.declarations.SClassDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.ACallObjectStmCG;
import org.overture.codegen.cgast.statements.APlainCallStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.vdm2c.utils.NameMangler;

public class CallRewriteTrans extends DepthFirstAnalysisAdaptor
{
	public TransAssistantCG assist;

	final static String retPrefix = "ret_";

	public CallRewriteTrans(TransAssistantCG assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseAReturnStmCG(AReturnStmCG node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseAReturnStmCG(node);
	}

	@Override
	public void caseAPlainCallStmCG(APlainCallStmCG node)
			throws AnalysisException
	{
		// op(a,d,f); --no root, so current class is the root.
		// TODO Auto-generated method stub

		SClassDeclCG cDef = node.getAncestor(SClassDeclCG.class);

		for (AMethodDeclCG m : cDef.getMethods())
		{
			if (NameMangler.getName(m.getName()).equals(node.getName())
					&& m.getFormalParams().size() - 1 == node.getArgs().size())
			{
				// this method could match
			}
		}

	}

	@Override
	public void caseAApplyExpCG(AApplyExpCG node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseAApplyExpCG(node);
	}

	@Override
	public void caseACallObjectExpStmCG(ACallObjectExpStmCG node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseACallObjectExpStmCG(node);
	}

	@Override
	public void caseACallObjectStmCG(ACallObjectStmCG node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseACallObjectStmCG(node);
	}
}