package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.ACharLiteralExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.AQuoteLiteralExpCG;
import org.overture.codegen.cgast.expressions.AStringLiteralExpCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class LiteralInstantiationRewriteTrans extends DepthFirstAnalysisAdaptor
{
	public TransAssistantCG assist;

	final static String retPrefix = "ret_";

	public LiteralInstantiationRewriteTrans(TransAssistantCG assist)
	{
		this.assist = assist;
	}

	private void rewriteToApply(SExpCG node, Object value, String string)
	{
		AApplyExpCG apply = newApply(string);
		assist.replaceNodeWith(node, apply);
		apply.getArgs().add(node);
	}

	@Override
	public void caseABoolLiteralExpCG(ABoolLiteralExpCG node)
			throws AnalysisException
	{
		rewriteToApply(node, node.getValue(), "newBool");
	}

	@Override
	public void caseACharLiteralExpCG(ACharLiteralExpCG node)
			throws AnalysisException
	{
		rewriteToApply(node, node.getValue(), "newChar");
	}

	@Override
	public void caseAIntLiteralExpCG(AIntLiteralExpCG node)
			throws AnalysisException
	{
		rewriteToApply(node, node.getValue(), "newInt");
	}

	@Override
	public void caseAQuoteLiteralExpCG(AQuoteLiteralExpCG node)
			throws AnalysisException
	{
		rewriteToApply(node, node.getValue(), "newQuote");
	}

	public void caseARealLiteralExpCG(
			org.overture.codegen.cgast.expressions.ARealLiteralExpCG node)
					throws AnalysisException
	{
		rewriteToApply(node, node.getValue(), "newReal");
	};

	@Override
	public void caseAStringLiteralExpCG(AStringLiteralExpCG node)
			throws AnalysisException
	{
		// TODO this may need additional rewrites
		rewriteToApply(node, node.getValue(), "newString");
	}
}