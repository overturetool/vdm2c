package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.rewriteToApply;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.intf.IAnalysis;
import org.overture.codegen.ir.expressions.AAbsUnaryExpIR;
import org.overture.codegen.ir.expressions.ADivideNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.AFloorUnaryExpIR;
import org.overture.codegen.ir.expressions.AGreaterEqualNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.AGreaterNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.AIntDivNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.ALessEqualNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.ALessNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.AMinusUnaryExpIR;
import org.overture.codegen.ir.expressions.AModNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.APlusNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.APowerNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.ARemNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.ASubtractNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.ATimesNumericBinaryExpIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.utils.IApplyAssistant;

/**
 * Transformation to rewrite all number operations to use the vdm library
 * 
 * @author kel
 */
public class NumericTrans extends DepthFirstAnalysisCAdaptor implements
		IApplyAssistant
{
	public static final String VDM_SUM = "vdmSum";
	public static final String VDM_LESS_THAN = "vdmLessThan";
	public static final String VDM_DIVISION = "vdmDivision";
	public static final String VDM_GREATER_OR_EQUAL = "vdmGreaterOrEqual";
	public static final String VDM_GREATER_THAN = "vdmGreaterThan";
	public static final String VDM_LESS_OR_EQUAL = "vdmLessOrEqual";
	public static final String VDM_MINUS = "vdmMinus";
	public static final String VDM_MOD = "vdmMod";
	public static final String VDM_PRODUCT = "vdmProduct";
	
	public static final String VDM_ABS = "vdmAbs";
	public static final String VDM_FLOOR = "vdmFloor";
	public static final String VDM_DIFFERENCE = "vdmDifference";
	public static final String VDM_DIV = "vdmDiv";
	public static final String VDM_REM = "vdmRem";
	public static final String VDM_POW = "vdmPower";
	
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
		rewriteToApply(this, node, VDM_SUM, node.getLeft(), node.getRight());
	}

	@Override
	public void caseALessNumericBinaryExpIR(ALessNumericBinaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, VDM_LESS_THAN, node.getLeft(), node.getRight());
	}

	@Override
	public void caseADivideNumericBinaryExpIR(ADivideNumericBinaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, VDM_DIVISION, node.getLeft(), node.getRight());
	}

	@Override
	public void caseAGreaterEqualNumericBinaryExpIR(
			AGreaterEqualNumericBinaryExpIR node) throws AnalysisException
	{
		rewriteToApply(this, node, VDM_GREATER_OR_EQUAL, node.getLeft(), node.getRight());
	}

	@Override
	public void caseAGreaterNumericBinaryExpIR(AGreaterNumericBinaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, VDM_GREATER_THAN, node.getLeft(), node.getRight());
	}

	@Override
	public void caseALessEqualNumericBinaryExpIR(
			ALessEqualNumericBinaryExpIR node) throws AnalysisException
	{
		rewriteToApply(this, node, VDM_LESS_OR_EQUAL, node.getLeft(), node.getRight());
	}

	@Override
	public void caseAMinusUnaryExpIR(AMinusUnaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, VDM_MINUS, node.getExp());
	}

	@Override
	public void caseAModNumericBinaryExpIR(AModNumericBinaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, VDM_MOD, node.getLeft(), node.getRight());
	}

	public void caseATimesNumericBinaryExpIR(ATimesNumericBinaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, VDM_PRODUCT, node.getLeft(), node.getRight());
	}
	
	@Override
	public void caseAAbsUnaryExpIR(AAbsUnaryExpIR node) throws AnalysisException
	{
		rewriteToApply(this, node, VDM_ABS, node.getExp());
	}

	@Override
	public void caseAFloorUnaryExpIR(AFloorUnaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, VDM_FLOOR, node.getExp());
	}
	
	@Override
	public void caseASubtractNumericBinaryExpIR(
			ASubtractNumericBinaryExpIR node) throws AnalysisException
	{
		rewriteToApply(this, node, VDM_DIFFERENCE, node.getLeft(), node.getRight());
	}
	
	@Override
	public void caseAIntDivNumericBinaryExpIR(AIntDivNumericBinaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, VDM_DIV, node.getLeft(), node.getRight());
	}
	
	@Override
	public void caseARemNumericBinaryExpIR(ARemNumericBinaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, VDM_REM, node.getLeft(), node.getRight());
	}
	
	@Override
	public void caseAPowerNumericBinaryExpIR(APowerNumericBinaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, VDM_POW, node.getLeft(), node.getRight());
	}
}
