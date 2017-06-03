package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.rewriteToApply;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.intf.IAnalysis;
import org.overture.codegen.ir.expressions.AAndBoolBinaryExpIR;
import org.overture.codegen.ir.expressions.AEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.ANotEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.ANotUnaryExpIR;
import org.overture.codegen.ir.expressions.AOrBoolBinaryExpIR;
import org.overture.codegen.ir.expressions.AXorBoolBinaryExpIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.utils.IApplyAssistant;

/**
 * Transformation to rewrite all number operations to use the vdm library
 * 
 * @author kel
 */
public class LogicTrans extends DepthFirstAnalysisCAdaptor implements
		IApplyAssistant
{
	public static final String VDM_OR = "vdmOr";
	public static final String VDM_NOT = "vdmNot";
	public static final String VDM_EQUALS = "vdmEquals";
	public static final String VDM_AND = "vdmAnd";
	public static final String VDM_XOR = "vdmXor";
	
	private TransAssistantIR assist;

	public LogicTrans(TransAssistantIR assist)
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
	public void caseAAndBoolBinaryExpIR(AAndBoolBinaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, VDM_AND, node.getLeft(), node.getRight());
	}

	@Override
	public void caseAEqualsBinaryExpIR(AEqualsBinaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, VDM_EQUALS, node.getLeft(), node.getRight());
	}

	@Override
	public void caseANotEqualsBinaryExpIR(ANotEqualsBinaryExpIR node)
			throws AnalysisException
	{
		SExpIR replacement = rewriteToApply(this, node, VDM_EQUALS, node.getLeft(), node.getRight());
		rewriteToApply(this, replacement, VDM_NOT, replacement).apply(THIS);;
//		replacement.parent().replaceChild(replacement, newApply("vdmNot", replacement.clone()));
	}

	@Override
	public void caseANotUnaryExpIR(ANotUnaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, VDM_NOT, node.getExp());
	}

	@Override
	public void caseAOrBoolBinaryExpIR(AOrBoolBinaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, VDM_OR, node.getLeft(), node.getRight());
	}
	
	@Override
	public void caseAXorBoolBinaryExpIR(AXorBoolBinaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, VDM_XOR, node.getLeft(), node.getRight());
	}
}