package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.rewriteToApply;

import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.analysis.intf.IAnalysis;
import org.overture.codegen.ir.expressions.ADivideNumericBinaryExpCG;
import org.overture.codegen.ir.expressions.AGreaterEqualNumericBinaryExpCG;
import org.overture.codegen.ir.expressions.AGreaterNumericBinaryExpCG;
import org.overture.codegen.ir.expressions.ALessEqualNumericBinaryExpCG;
import org.overture.codegen.ir.expressions.ALessNumericBinaryExpCG;
import org.overture.codegen.ir.expressions.AMinusUnaryExpCG;
import org.overture.codegen.ir.expressions.AModNumericBinaryExpCG;
import org.overture.codegen.ir.expressions.APlusNumericBinaryExpCG;
import org.overture.codegen.ir.expressions.ATimesNumericBinaryExpCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.vdm2c.utils.IApplyAssistant;

/**
 * Transformation to rewrite all number operations to use the vdm library
 * 
 * @author kel
 */
public class NumericTrans extends DepthFirstAnalysisAdaptor implements
		IApplyAssistant
{
	public TransAssistantCG assist;

	public NumericTrans(TransAssistantCG assist)
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
	public void caseAPlusNumericBinaryExpCG(APlusNumericBinaryExpCG node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "vdmSum", node.getLeft(), node.getRight());
	}

	@Override
	public void caseALessNumericBinaryExpCG(ALessNumericBinaryExpCG node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "vdmLessThan", node.getLeft(), node.getRight());
	}

	@Override
	public void caseADivideNumericBinaryExpCG(ADivideNumericBinaryExpCG node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "vdmDivision", node.getLeft(), node.getRight());
	}

	@Override
	public void caseAGreaterEqualNumericBinaryExpCG(
			AGreaterEqualNumericBinaryExpCG node) throws AnalysisException
	{
		rewriteToApply(this, node, "vdmGreaterOrEqual", node.getLeft(), node.getRight());
	}

	@Override
	public void caseAGreaterNumericBinaryExpCG(AGreaterNumericBinaryExpCG node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "vdmGreaterThan", node.getLeft(), node.getRight());
	}

	@Override
	public void caseALessEqualNumericBinaryExpCG(
			ALessEqualNumericBinaryExpCG node) throws AnalysisException
	{
		rewriteToApply(this, node, "vdmLessOrEqual", node.getLeft(), node.getRight());
	}

	@Override
	public void caseAMinusUnaryExpCG(AMinusUnaryExpCG node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "vdmMinus", node.getExp());
	}

	@Override
	public void caseAModNumericBinaryExpCG(AModNumericBinaryExpCG node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "vdmMod", node.getLeft(), node.getRight());
	}

	public void caseATimesNumericBinaryExpCG(ATimesNumericBinaryExpCG node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "vdmProduct", node.getLeft(), node.getRight());
	};

}