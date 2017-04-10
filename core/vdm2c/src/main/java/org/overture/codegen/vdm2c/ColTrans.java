package org.overture.codegen.vdm2c;

import static org.overture.codegen.vdm2c.utils.CTransUtil.rewriteToApply;

import java.util.LinkedList;
import java.util.List;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.intf.IAnalysis;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.ACardUnaryExpIR;
import org.overture.codegen.ir.expressions.ADistIntersectUnaryExpIR;
import org.overture.codegen.ir.expressions.ADistUnionUnaryExpIR;
import org.overture.codegen.ir.expressions.AEnumMapExpIR;
import org.overture.codegen.ir.expressions.AEnumSeqExpIR;
import org.overture.codegen.ir.expressions.AEnumSetExpIR;
import org.overture.codegen.ir.expressions.AExternalExpIR;
import org.overture.codegen.ir.expressions.AHeadUnaryExpIR;
import org.overture.codegen.ir.expressions.AInSetBinaryExpIR;
import org.overture.codegen.ir.expressions.ALenUnaryExpIR;
import org.overture.codegen.ir.expressions.AMapDomainUnaryExpIR;
import org.overture.codegen.ir.expressions.AMapletExpIR;
import org.overture.codegen.ir.expressions.APowerSetUnaryExpIR;
import org.overture.codegen.ir.expressions.AReverseUnaryExpIR;
import org.overture.codegen.ir.expressions.ASeqConcatBinaryExpIR;
import org.overture.codegen.ir.expressions.ASetDifferenceBinaryExpIR;
import org.overture.codegen.ir.expressions.ASetIntersectBinaryExpIR;
import org.overture.codegen.ir.expressions.ASetProperSubsetBinaryExpIR;
import org.overture.codegen.ir.expressions.ASetSubsetBinaryExpIR;
import org.overture.codegen.ir.expressions.ASetUnionBinaryExpIR;
import org.overture.codegen.ir.expressions.ATailUnaryExpIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.utils.CLetBeStStrategy;
import org.overture.codegen.vdm2c.utils.IApplyAssistant;

/**
 *
 * TODO: Extend this class to handle sets and maps too (rename class
 * accordingly)
 * 
 */
public class ColTrans extends DepthFirstAnalysisCAdaptor implements IApplyAssistant {

	public static final String SEQ_VAR = "newSeqVar";
	public static final String SET_VAR = "newSetVar";
	public static final String MAP_VAR = "newMapVarToGrow";	
	
	// Sequence operations
	public static final String SEQ_TAIL = "vdmSeqTl";
	public static final String SEQ_LEN = "vdmSeqLen";
	public static final String SEQ_HEAD = "vdmSeqHd";
	public static final String SEQ_CONC = "vdmSeqConc";
	public static final String SEQ_REVERSE = "vdmSeqReverse";
	public static final String SEQ_INDEX = "vdmSeqIndex";
	
	// Set operations
	public static final String SET_MEMBER = "vdmSetMemberOf";
	public static final String SET_UNION = "vdmSetUnion";
	public static final String SET_INTER = "vdmSetInter";
	public static final String SET_DIFF = "vdmSetDifference";
	public static final String SET_SUBSET = "vdmSetSubset";
	public static final String SET_PROPER_SUBSET = "vdmSetProperSubset";
	public static final String SET_DIST_UNION = "vdmSetDunion";
	public static final String SET_DIST_INTER = "vdmSetDinter";
	public static final String SET_POWER_SET = "vdmSetPower";
	
	// Map operations
	public static final String MAP_DOM = "vdmMapDom";

	private TransAssistantIR assist;

	public ColTrans(TransAssistantIR assist) {
		this.assist = assist;
	}

	@Override
	public TransAssistantIR getAssist() {
		return assist;
	}

	@Override
	public IAnalysis getAnalyzer() {
		return THIS;
	}
	
	@Override
	public void caseALenUnaryExpIR(ALenUnaryExpIR node) throws AnalysisException {

		rewriteToApply(this, node, SEQ_LEN, node.getExp());
	}
	
	@Override
	public void caseATailUnaryExpIR(ATailUnaryExpIR node) throws AnalysisException {

		rewriteToApply(this, node, SEQ_TAIL, node.getExp());
	}
	
	@Override
	public void caseAHeadUnaryExpIR(AHeadUnaryExpIR node) throws AnalysisException {
		
		rewriteToApply(this, node, SEQ_HEAD, node.getExp());
	}

	@Override
	public void caseASeqConcatBinaryExpIR(ASeqConcatBinaryExpIR node) throws AnalysisException {

		rewriteToApply(this, node, SEQ_CONC, node.getLeft(), node.getRight());
	}
	
	@Override
	public void caseAReverseUnaryExpIR(AReverseUnaryExpIR node) throws AnalysisException {

		rewriteToApply(this, node, SEQ_REVERSE, node.getExp());
	}
	
	@Override
	public void caseAApplyExpIR(AApplyExpIR node) throws AnalysisException {
	
		if(assist.getInfo().getTypeAssistant().isSeqType(node.getRoot()) && node.getArgs().size() == 1)
		{
			rewriteToApply(this, node, SEQ_INDEX, node.getRoot(), node.getArgs().getFirst());
		}
		else
		{
			super.caseAApplyExpIR(node);
		}
	}
	
	@Override
	public void caseAEnumSeqExpIR(AEnumSeqExpIR node) throws AnalysisException {

		rewriteColEnumToApply(node, SEQ_VAR, node.getMembers());
	}

	@Override
	public void caseAEnumSetExpIR(AEnumSetExpIR node) throws AnalysisException {

		rewriteColEnumToApply(node, SET_VAR, node.getMembers());
	}
	
	@Override
	public void caseAEnumMapExpIR(AEnumMapExpIR node) throws AnalysisException {

		List<SExpIR> flattened = new LinkedList<>();

		for (AMapletExpIR e : node.getMembers()) {
			flattened.add(e.getLeft());
			flattened.add(e.getRight());
		}

		rewriteColEnumToApply(node, MAP_VAR, flattened);
	}
	
	@Override
	public void caseAInSetBinaryExpIR(AInSetBinaryExpIR node) throws AnalysisException {

		rewriteToApply(this, node, SET_MEMBER, node.getRight(), node.getLeft());
	}
	
	@Override
	public void caseASetUnionBinaryExpIR(ASetUnionBinaryExpIR node) throws AnalysisException {
		
		rewriteToApply(this, node, SET_UNION, node.getLeft(), node.getRight());
	}
	
	@Override
	public void caseASetIntersectBinaryExpIR(ASetIntersectBinaryExpIR node) throws AnalysisException {

		rewriteToApply(this, node, SET_INTER, node.getLeft(), node.getRight());
	}

	@Override
	public void caseASetDifferenceBinaryExpIR(ASetDifferenceBinaryExpIR node) throws AnalysisException {

		rewriteToApply(this, node, SET_DIFF, node.getLeft(), node.getRight());
	}
	
	@Override
	public void caseASetSubsetBinaryExpIR(ASetSubsetBinaryExpIR node) throws AnalysisException {
		
		rewriteToApply(this, node, SET_SUBSET, node.getLeft(), node.getRight());
	}
	
	@Override
	public void caseASetProperSubsetBinaryExpIR(ASetProperSubsetBinaryExpIR node) throws AnalysisException {
		
		rewriteToApply(this, node, SET_PROPER_SUBSET, node.getLeft(), node.getRight());
	}
	
	@Override
	public void caseACardUnaryExpIR(ACardUnaryExpIR node) throws AnalysisException {
		
		rewriteToApply(this, node, CLetBeStStrategy.SET_CARD, node.getExp());
	}
	
	@Override
	public void caseADistUnionUnaryExpIR(ADistUnionUnaryExpIR node) throws AnalysisException {
		
		rewriteToApply(this, node, SET_DIST_UNION, node.getExp());
	}
	
	@Override
	public void caseADistIntersectUnaryExpIR(ADistIntersectUnaryExpIR node) throws AnalysisException {
		
		rewriteToApply(this, node, SET_DIST_INTER, node.getExp());
	}
	
	@Override
	public void caseAPowerSetUnaryExpIR(APowerSetUnaryExpIR node) throws AnalysisException {
		
		rewriteToApply(this, node, SET_POWER_SET, node.getExp());
	}
	
	@Override
	public void caseAMapDomainUnaryExpIR(AMapDomainUnaryExpIR node) throws AnalysisException {
		
		rewriteToApply(this, node, MAP_DOM, node.getExp());
	}
	
	private void rewriteColEnumToApply(SExpIR node, String seqVar, List<SExpIR> members)
			throws AnalysisException {
		AExternalTypeIR extType = new AExternalTypeIR();
		extType.setName("size_t");

		AExternalExpIR size = new AExternalExpIR();
		size.setTargetLangExp(members.size() + "");

		LinkedList<SExpIR> argList = new LinkedList<>(members);
		argList.addFirst(size);

		if (members.isEmpty()) {
			argList.add(assist.getInfo().getExpAssistant().consNullExp());
		}

		SExpIR[] args = argList.toArray(new SExpIR[0]);

		rewriteToApply(this, node, seqVar, args);
	}
}
