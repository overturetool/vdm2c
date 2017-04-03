package org.overture.codegen.vdm2c;

import static org.overture.codegen.vdm2c.utils.CTransUtil.rewriteToApply;

import java.util.LinkedList;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.intf.IAnalysis;
import org.overture.codegen.ir.expressions.AEnumSeqExpIR;
import org.overture.codegen.ir.expressions.AExternalExpIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.utils.IApplyAssistant;

/**
 *
 * TODO: Extend this class to handle sets and maps too (rename class accordingly)
 * 
 */
public class SeqTrans extends DepthFirstAnalysisCAdaptor implements
IApplyAssistant {

	public static final String SEQ_VAR = "newSeqVar";
	
	private TransAssistantIR assist;
	
	public SeqTrans(TransAssistantIR assist)
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
	public void caseAEnumSeqExpIR(AEnumSeqExpIR node) throws AnalysisException {

		AExternalTypeIR extType = new AExternalTypeIR();
		extType.setName("size_t");
		
		AExternalExpIR size = new AExternalExpIR();
		size.setTargetLangExp(node.getMembers().size() + "");
		
		LinkedList<SExpIR> argList = new LinkedList<>(node.getMembers());
		argList.addFirst(size);
		
		if(node.getMembers().isEmpty())
		{
			argList.add(assist.getInfo().getExpAssistant().consNullExp());
		}
		
		SExpIR[] args = argList.toArray(new SExpIR[0]);
		
		rewriteToApply(this, node, SEQ_VAR, args);
	}
}
