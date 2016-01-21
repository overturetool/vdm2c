package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ALessNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.APlusNumericBinaryExpCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class NumericTrans extends DepthFirstAnalysisAdaptor
{
	public TransAssistantCG assist;

	final static String retPrefix = "ret_";

	public NumericTrans(TransAssistantCG assist)
	{
		this.assist = assist;
	}

	private void rewriteToApply(SExpCG node, String string, SExpCG... args) throws AnalysisException
	{
		AApplyExpCG apply = newApply(string);
		assist.replaceNodeWith(node, apply);
		for (SExpCG arg : args)
		{
			apply.getArgs().add(arg);
			arg.apply(THIS);
		}
		
	}
	
	
	@Override
	public void caseAPlusNumericBinaryExpCG(APlusNumericBinaryExpCG node)
			throws AnalysisException
	{
		rewriteToApply(node,  "vdmSum",node.getLeft(),node.getRight());
	}
	
	@Override
	public void caseALessNumericBinaryExpCG(ALessNumericBinaryExpCG node)
			throws AnalysisException
	{
		rewriteToApply(node,  "vdmLessThan",node.getLeft(),node.getRight());
	}

}