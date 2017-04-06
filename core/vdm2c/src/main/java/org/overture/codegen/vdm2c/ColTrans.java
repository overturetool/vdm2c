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
public class ColTrans extends DepthFirstAnalysisCAdaptor implements
IApplyAssistant {

	public static final String SEQ_VAR = "newSeqVar";
	
	private TransAssistantIR assist;
	
	public ColTrans(TransAssistantIR assist)
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

		rewriteColEnumToApply(node, SEQ_VAR, node.getMembers());
	}

	private void rewriteColEnumToApply(AEnumSeqExpIR node, String seqVar, LinkedList<SExpIR> members) throws AnalysisException {
		AExternalTypeIR extType = new AExternalTypeIR();
		extType.setName("size_t");
		
		AExternalExpIR size = new AExternalExpIR();
		size.setTargetLangExp(members.size() + "");
		
		LinkedList<SExpIR> argList = new LinkedList<>(members);
		argList.addFirst(size);
		
		if(members.isEmpty())
		{
			argList.add(assist.getInfo().getExpAssistant().consNullExp());
		}
		
		SExpIR[] args = argList.toArray(new SExpIR[0]);
		
		rewriteToApply(this, node, seqVar, args);
	}
}
