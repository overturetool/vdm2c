package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.rewriteToApply;

import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.analysis.intf.IAnalysis;
import org.overture.codegen.ir.expressions.ADivideNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.AGreaterEqualNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.AGreaterNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.ALessEqualNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.ALessNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.AMinusUnaryExpIR;
import org.overture.codegen.ir.expressions.AModNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.APlusNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.ATimesNumericBinaryExpIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.utils.IApplyAssistant;

/**
 * Transformation to rewrite all number operations to use the vdm library
 * 
 * @author kel
 */
public class NumericTrans extends DepthFirstAnalysisAdaptor implements
		IApplyAssistant
{
	public TransAssistantIR assist;

	public NumericTrans(TransAssistantIR assist)
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
	public void caseAPlusNumericBinaryExpIR(APlusNumericBinaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "vdmSum", node.getLeft(), node.getRight());
	}

	@Override
	public void caseALessNumericBinaryExpIR(ALessNumericBinaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "vdmLessThan", node.getLeft(), node.getRight());
	}

	@Override
	public void caseADivideNumericBinaryExpIR(ADivideNumericBinaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "vdmDivision", node.getLeft(), node.getRight());
	}

	@Override
	public void caseAGreaterEqualNumericBinaryExpIR(
			AGreaterEqualNumericBinaryExpIR node) throws AnalysisException
	{
		rewriteToApply(this, node, "vdmGreaterOrEqual", node.getLeft(), node.getRight());
	}

	@Override
	public void caseAGreaterNumericBinaryExpIR(AGreaterNumericBinaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "vdmGreaterThan", node.getLeft(), node.getRight());
	}

	@Override
	public void caseALessEqualNumericBinaryExpIR(
			ALessEqualNumericBinaryExpIR node) throws AnalysisException
	{
		rewriteToApply(this, node, "vdmLessOrEqual", node.getLeft(), node.getRight());
	}

	@Override
	public void caseAMinusUnaryExpIR(AMinusUnaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "vdmMinus", node.getExp());
	}

	@Override
	public void caseAModNumericBinaryExpIR(AModNumericBinaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "vdmMod", node.getLeft(), node.getRight());
	}

	public void caseATimesNumericBinaryExpIR(ATimesNumericBinaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "vdmProduct", node.getLeft(), node.getRight());
	};

}