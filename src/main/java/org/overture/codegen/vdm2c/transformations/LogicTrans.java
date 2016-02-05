package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.rewriteToApply;

import org.overture.codegen.ir.SExpCG;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.analysis.intf.IAnalysis;
import org.overture.codegen.ir.expressions.AAndBoolBinaryExpCG;
import org.overture.codegen.ir.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.ir.expressions.ANotEqualsBinaryExpCG;
import org.overture.codegen.ir.expressions.ANotUnaryExpCG;
import org.overture.codegen.ir.expressions.AOrBoolBinaryExpCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.vdm2c.utils.IApplyAssistant;

/**
 * Transformation to rewrite all number operations to use the vdm library
 * 
 * @author kel
 */
public class LogicTrans extends DepthFirstAnalysisAdaptor implements
		IApplyAssistant
{
	public TransAssistantCG assist;

	public LogicTrans(TransAssistantCG assist)
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
	public void caseAAndBoolBinaryExpCG(AAndBoolBinaryExpCG node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "vdmAnd", node.getLeft(), node.getRight());
	}

	@Override
	public void caseAEqualsBinaryExpCG(AEqualsBinaryExpCG node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "vdmEquals", node.getLeft(), node.getRight());
	}

	@Override
	public void caseANotEqualsBinaryExpCG(ANotEqualsBinaryExpCG node)
			throws AnalysisException
	{
		SExpCG replacement = rewriteToApply(this, node, "vdmEquals", node.getLeft(), node.getRight());
		replacement.parent().replaceChild(replacement, newApply("vdmNot", replacement.clone()));
	}

	@Override
	public void caseANotUnaryExpCG(ANotUnaryExpCG node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "vdmNot", node.getExp());
	}

	@Override
	public void caseAOrBoolBinaryExpCG(AOrBoolBinaryExpCG node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "vdmOr", node.getLeft(), node.getRight());
	}

}