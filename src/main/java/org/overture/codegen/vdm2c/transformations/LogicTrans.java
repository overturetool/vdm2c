package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
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
	public TransAssistantIR assist;

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
		rewriteToApply(this, node, "vdmAnd", node.getLeft(), node.getRight());
	}

	@Override
	public void caseAEqualsBinaryExpIR(AEqualsBinaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "vdmEquals", node.getLeft(), node.getRight());
	}

	@Override
	public void caseANotEqualsBinaryExpIR(ANotEqualsBinaryExpIR node)
			throws AnalysisException
	{
		SExpIR replacement = rewriteToApply(this, node, "vdmEquals", node.getLeft(), node.getRight());
		rewriteToApply(this, replacement, "vdmNot", replacement).apply(THIS);;
//		replacement.parent().replaceChild(replacement, newApply("vdmNot", replacement.clone()));
	}

	@Override
	public void caseANotUnaryExpIR(ANotUnaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "vdmNot", node.getExp());
	}

	@Override
	public void caseAOrBoolBinaryExpIR(AOrBoolBinaryExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, "vdmOr", node.getLeft(), node.getRight());
	}

}