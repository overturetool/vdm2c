package org.overture.codegen.vdm2c;

import static org.overture.codegen.vdm2c.utils.CTransUtil.rewriteToApply;

import java.util.LinkedList;
import java.util.List;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.intf.IAnalysis;
import org.overture.codegen.ir.expressions.AExternalExpIR;
import org.overture.codegen.ir.expressions.ATupleExpIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.utils.IApplyAssistant;

public class TupleTrans  extends DepthFirstAnalysisCAdaptor implements IApplyAssistant {

	public static final String TUPLE_EXP = "newProductVar";
	
	private TransAssistantIR assist;

	public TupleTrans(TransAssistantIR assist) {
		this.assist = assist;
	}
	
	@Override
	public void caseATupleExpIR(ATupleExpIR node) throws AnalysisException {

		AExternalTypeIR extType = new AExternalTypeIR();
		extType.setName("size_t");

		AExternalExpIR sizeExp = new AExternalExpIR();		
		sizeExp.setTargetLangExp(node.getArgs().size() + "");
		
		List<SExpIR> args = new LinkedList<>();
		args.add(sizeExp);
		
		args.addAll(node.getArgs());
		
		rewriteToApply(this, node, TUPLE_EXP, args.toArray(new SExpIR[0]));
	}
	
	@Override
	public TransAssistantIR getAssist() {
		return assist;
	}

	@Override
	public IAnalysis getAnalyzer() {
		return THIS;
	}
}
