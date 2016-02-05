package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.rewriteToApply;

import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.analysis.intf.IAnalysis;
import org.overture.codegen.ir.expressions.ABoolLiteralExpIR;
import org.overture.codegen.ir.expressions.ACharLiteralExpIR;
import org.overture.codegen.ir.expressions.AIntLiteralExpIR;
import org.overture.codegen.ir.expressions.AQuoteLiteralExpIR;
import org.overture.codegen.ir.expressions.AStringLiteralExpIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.utils.IApplyAssistant;

/**
 * Analysis to rewrite all literal instantiations to apply expressions using the vdm library create functions
 * 
 * @author kel
 */
public class LiteralInstantiationRewriteTrans extends DepthFirstAnalysisAdaptor
		implements IApplyAssistant
{
	public TransAssistantIR assist;

	public LiteralInstantiationRewriteTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override
	public TransAssistantIR getAssist()
	{
		return assist;
	}

	@Override
	public IAnalysis getAnalyzer()
	{
		return THIS;
	}

	@Override
	public void caseABoolLiteralExpIR(ABoolLiteralExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "newBool", node);
	}

	@Override
	public void caseACharLiteralExpIR(ACharLiteralExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "newChar", node);
	}

	@Override
	public void caseAIntLiteralExpIR(AIntLiteralExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "newInt", node);
	}

	@Override
	public void caseAQuoteLiteralExpIR(AQuoteLiteralExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "newQuote", node);
	}

	public void caseARealLiteralExpIR(
			org.overture.codegen.ir.expressions.ARealLiteralExpIR node)
					throws AnalysisException
	{
		rewriteToApply(this, node, "newReal", node);
	};

	@Override
	public void caseAStringLiteralExpIR(AStringLiteralExpIR node)
			throws AnalysisException
	{
		// TODO this may need additional rewrites actually this must be a seq comprehension
		rewriteToApply(this, node, "newString");
	}

}