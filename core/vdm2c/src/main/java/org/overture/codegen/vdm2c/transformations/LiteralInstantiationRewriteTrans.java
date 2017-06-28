package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.rewriteToApply;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.intf.IAnalysis;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.utils.IApplyAssistant;

/**
 * Analysis to rewrite all literal instantiations to apply expressions using the vdm library create functions
 * 
 * @author kel
 */
public class LiteralInstantiationRewriteTrans extends
		DepthFirstAnalysisCAdaptor implements IApplyAssistant
{
	public static final String NEW_STRING = "newString";
	public static final String NEW_REAL = "newReal";
	public static final String NEW_QUOTE = "newQuote";
	public static final String NEW_INT = "newInt";
	public static final String NEW_CHAR = "newChar";
	public static final String NEW_BOOL = "newBool";
	public static final String NEW_TOKEN = "newToken";
	public static final String NEW_UNKNOWN = "newUnknown";

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
		rewriteToApply(this, node, NEW_BOOL, node);
	}

	@Override
	public void caseACharLiteralExpIR(ACharLiteralExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, NEW_CHAR, node);
	}

	@Override
	public void caseAIntLiteralExpIR(AIntLiteralExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, NEW_INT, node);
	}

	@Override
	public void caseAQuoteLiteralExpIR(AQuoteLiteralExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, NEW_QUOTE, node);
	}

	public void caseARealLiteralExpIR(
			org.overture.codegen.ir.expressions.ARealLiteralExpIR node)
			throws AnalysisException
	{
		rewriteToApply(this, node, NEW_REAL, node);
	}
	
	@Override
	public void caseAStringLiteralExpIR(AStringLiteralExpIR node)
			throws AnalysisException
	{
		// TODO this may need additional rewrites actually this must be a seq comprehension
		rewriteToApply(this, node, NEW_STRING);
	}

	@Override
	public void caseAMkBasicExpIR(AMkBasicExpIR node) throws AnalysisException {
		
		SExpIR arg = node.getArg();
		
		AApplyExpIR apply = rewriteToApply(this,  node, NEW_TOKEN);
		
		apply.getArgs().add(arg.clone());
		
		apply.getArgs().getFirst().apply(this);
	}

	@Override
	public void caseANullExpIR(ANullExpIR node) throws AnalysisException {

		rewriteToApply(this, node, NEW_UNKNOWN);
	}
}