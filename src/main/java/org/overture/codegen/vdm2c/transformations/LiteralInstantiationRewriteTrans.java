package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.rewriteToApply;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.analysis.intf.IAnalysis;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.ACharLiteralExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.AQuoteLiteralExpCG;
import org.overture.codegen.cgast.expressions.AStringLiteralExpCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.vdm2c.utils.IApplyAssistant;

/**
 * Analysis to rewrite all literal instantiations to apply expressions using the vdm library create functions
 * 
 * @author kel
 */
public class LiteralInstantiationRewriteTrans extends DepthFirstAnalysisAdaptor
		implements IApplyAssistant
{
	public TransAssistantCG assist;

	public LiteralInstantiationRewriteTrans(TransAssistantCG assist)
	{
		this.assist = assist;
	}

	@Override
	public TransAssistantCG getAssist()
	{
		return assist;
	}

	@Override
	public IAnalysis getAnalyzer()
	{
		return THIS;
	}

	@Override
	public void caseABoolLiteralExpCG(ABoolLiteralExpCG node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "newBool", node);
	}

	@Override
	public void caseACharLiteralExpCG(ACharLiteralExpCG node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "newChar", node);
	}

	@Override
	public void caseAIntLiteralExpCG(AIntLiteralExpCG node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "newInt", node);
	}

	@Override
	public void caseAQuoteLiteralExpCG(AQuoteLiteralExpCG node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "newQuote", node);
	}

	public void caseARealLiteralExpCG(
			org.overture.codegen.cgast.expressions.ARealLiteralExpCG node)
					throws AnalysisException
	{
		rewriteToApply(this, node, "newReal", node);
	};

	@Override
	public void caseAStringLiteralExpCG(AStringLiteralExpCG node)
			throws AnalysisException
	{
		// TODO this may need additional rewrites actually this must be a seq comprehension
		rewriteToApply(this, node, "newString");
	}

}