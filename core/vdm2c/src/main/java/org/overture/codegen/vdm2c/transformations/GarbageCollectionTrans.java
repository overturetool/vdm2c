package org.overture.codegen.vdm2c.transformations;

import java.util.HashMap;
import java.util.LinkedList;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.CForIterator;
import org.overture.codegen.vdm2c.ColTrans;
import org.overture.codegen.vdm2c.TupleTrans;
import org.overture.codegen.vdm2c.Vdm2cTag;
import org.overture.codegen.vdm2c.Vdm2cTag.MethodTag;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;
import org.overture.codegen.vdm2c.utils.CGenUtil;
import org.overture.codegen.vdm2c.utils.CLetBeStStrategy;
import org.overture.codegen.vdm2c.utils.CSetCompStrategy;
import org.overture.codegen.vdm2c.utils.CTransUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GarbageCollectionTrans extends DepthFirstAnalysisCAdaptor
{
	final static Logger logger = LoggerFactory.getLogger(GarbageCollectionTrans.class);

	private HashMap<String, String> gcNames;

	private TransAssistantIR assist;

	public static final String SELF_GC = "SELF_GC";
	
	public GarbageCollectionTrans(TransAssistantIR assist)
	{
		this.gcNames = new HashMap<>();
		this.assist = assist;
		initNames();
	}

	public void initNames()
	{
		// Numeric unary operators
		gcNames.put(NumericTrans.VDM_MINUS, "vdmMinusGC");
		gcNames.put(NumericTrans.VDM_ABS, "vdmAbsGC");
		gcNames.put(NumericTrans.VDM_FLOOR, "vdmFloorGC");
	
		// Boolean binary expressions
		gcNames.put(LogicTrans.VDM_NOT, "vdmNotGC");
		gcNames.put(LogicTrans.VDM_OR, "vdmOrGC");
		gcNames.put(LogicTrans.VDM_AND, "vdmAndGC");
		gcNames.put(LogicTrans.VDM_XOR, "vdmXorGC");
		
		// Numeric binary expressions
		gcNames.put(NumericTrans.VDM_SUM, "vdmSumGC");
		gcNames.put(NumericTrans.VDM_DIFFERENCE, "vdmDifferenceGC");
		gcNames.put(NumericTrans.VDM_PRODUCT, "vdmProductGC");
		gcNames.put(NumericTrans.VDM_DIVISION, "vdmDivisionGC");
		gcNames.put(NumericTrans.VDM_DIV, "vdmDivGC");
		gcNames.put(NumericTrans.VDM_REM, "vdmRemGC");
		gcNames.put(NumericTrans.VDM_MOD, "vdmModGC");
		gcNames.put(NumericTrans.VDM_POW, "vdmPowerGC");

		// "is" checks
		gcNames.put(IsCheckTrans.VDM_IS_NAT, "isNatGC");
		gcNames.put(IsCheckTrans.VDM_IS_NAT1, "isNat1GC");
		gcNames.put(IsCheckTrans.VDM_IS_INT, "isIntGC");
		gcNames.put(IsCheckTrans.VDM_IS_BOOL, "isBoolGC");
		gcNames.put(IsCheckTrans.VDM_IS_RAT, "isRatGC");

		// Numeric comparison
		gcNames.put(NumericTrans.VDM_GREATER_THAN, "vdmGreaterThanGC");
		gcNames.put(NumericTrans.VDM_GREATER_OR_EQUAL, "vdmGreaterOrEqualGC");
		gcNames.put(NumericTrans.VDM_LESS_THAN, "vdmLessThanGC");
		gcNames.put(NumericTrans.VDM_LESS_OR_EQUAL, "vdmLessOrEqualGC");
		
		// Equality
		gcNames.put(LogicTrans.VDM_EQUALS, "vdmEqualsGC");
		
		// Value constructors
		gcNames.put(LiteralInstantiationRewriteTrans.NEW_INT, "newIntGC");
		gcNames.put(LiteralInstantiationRewriteTrans.NEW_BOOL, "newBoolGC");
		gcNames.put(LiteralInstantiationRewriteTrans.NEW_REAL, "newRealGC");
		gcNames.put(LiteralInstantiationRewriteTrans.NEW_CHAR, "newCharGC");
		gcNames.put(LiteralInstantiationRewriteTrans.NEW_QUOTE, "newQuoteGC");
		gcNames.put(LiteralInstantiationRewriteTrans.NEW_TOKEN, "newTokenGC");
		gcNames.put(LiteralInstantiationRewriteTrans.NEW_UNKNOWN, "newUnknownGC");

		// Collections
		gcNames.put(ColTrans.SEQ_VAR, "newSeqVarGC");
		gcNames.put(ColTrans.SET_VAR, "newSetVarGC");
		gcNames.put(ColTrans.MAP_VAR, "newMapVarGC");
		
		// Sequence operations
		gcNames.put(ColTrans.SEQ_TAIL, "vdmSeqTlGC");
		gcNames.put(ColTrans.SEQ_LEN, "vdmSeqLenGC");
		gcNames.put(ColTrans.SEQ_HEAD, "vdmSeqHdGC");
		gcNames.put(ColTrans.SEQ_CONC, "vdmSeqConcGC");
		gcNames.put(ColTrans.SEQ_REVERSE, "vdmSeqReverseGC");
		gcNames.put(ColTrans.SEQ_INDEX, "vdmSeqIndexGC");
		gcNames.put(ColTrans.SEQ_ELEMS, "vdmSeqElemsGC");
		gcNames.put(ColTrans.SEQ_INDS, "vdmSeqIndsGC");
		gcNames.put(ColTrans.SEQ_MOD, "vdmSeqModGC");

		// Set operations and utility functions
		gcNames.put(CForIterator.VDM_SET_ELEMENT_AT, "vdmSetElementAtGC");
		gcNames.put(ColTrans.SET_MEMBER, "vdmSetMemberOfGC");
		gcNames.put(ColTrans.SET_UNION, "vdmSetUnionGC");
		gcNames.put(ColTrans.SET_INTER, "vdmSetInterGC");
		gcNames.put(ColTrans.SET_DIFF, "vdmSetDifferenceGC");
		gcNames.put(ColTrans.SET_SUBSET, "vdmSetSubsetGC");
		gcNames.put(ColTrans.SET_PROPER_SUBSET, "vdmSetProperSubsetGC");
		gcNames.put(CLetBeStStrategy.SET_CARD, "vdmSetCardGC");
		gcNames.put(ColTrans.SET_DIST_UNION, "vdmSetDunionGC");
		gcNames.put(ColTrans.SET_DIST_INTER, "vdmSetDinterGC");
		gcNames.put(ColTrans.SET_POWER_SET, "vdmSetPowerGC");
		
		// Comprehensions
		gcNames.put(CSetCompStrategy.NEW_SET_VAR_TO_GROW, "newSetVarToGrowGC");
		
		// Map operations
		gcNames.put(ColTrans.MAP_DOM, "vdmMapDomGC");
		gcNames.put(ColTrans.MAP_RNG, "vdmMapRngGC");
		gcNames.put(ColTrans.MAP_UNION, "vdmMapMunionGC");
		gcNames.put(ColTrans.MAP_APPLY, "vdmMapApplyGC");
		gcNames.put(ColTrans.MAP_OVERRIDE, "vdmMapOverrideGC");
		gcNames.put(ColTrans.MAP_DIST_MERGE, "vdmMapMergeGC");
		gcNames.put(ColTrans.MAP_RES_DOM_TO, "vdmMapDomRestrictToGC");
		gcNames.put(ColTrans.MAP_RES_DOM_BY, "vdmMapDomRestrictByGC");
		gcNames.put(ColTrans.MAP_RES_RNG_TO, "vdmMapRngRestrictToGC");
		gcNames.put(ColTrans.MAP_RES_RNG_BY, "vdmMapRngRestrictByGC");
		
		// Tuples
		gcNames.put(TupleTrans.TUPLE_EXP, "newProductVarGC");
		gcNames.put(TupleTrans.TUPLE_FIELD_NUMBER_EXP, "productGetGC");
		
		// Copying
		gcNames.put(ValueSemantics.VDM_CLONE, "vdmCloneGC");
		
		// Accessors
		gcNames.put(CTransUtil.GET_FIELD, "GET_FIELD_GC");
		gcNames.put(CTransUtil.GET_FIELD_PTR, "GET_FIELD_PTR_GC");
		
		// Others
		gcNames.put(SelfTrans.SELF, SELF_GC);
	}

	@Override
	public void caseAMacroApplyExpIR(AMacroApplyExpIR node)
			throws AnalysisException
	{
		// Used to handle GET and SET macros
		super.caseAMacroApplyExpIR(node);
		
		if(insideFieldInitializer(node))
		{
			return;
		}
		
		changeToGcCall(node, node.getArgs(), node.getRoot());
	}
	
	@Override
	public void caseAApplyExpIR(AApplyExpIR node) throws AnalysisException
	{
		super.caseAApplyExpIR(node);

		if(insideFieldInitializer(node))
		{
			return;
		}
		
		changeToGcCall(node, node.getArgs(), node.getRoot());
	}

	private void changeToGcCall(SExpIR node, LinkedList<SExpIR> args,
			SExpIR root)
	{
		if (root instanceof AIdentifierVarExpIR)
		{
			AIdentifierVarExpIR id = (AIdentifierVarExpIR) root;

			String oldName = id.getName();
			
			String val = gcNames.get(oldName);

			if (val != null)
			{
				id.setName(val);
				if(!(isFieldAccessor(oldName) || isSetter(oldName)))
				{
					if (isSeqOrSet(oldName) || isTupleExp(oldName)) {
						// The signatures of 'newSeqVarGC', 'newSetVarGC' and 'newProductVarGC' are:
						// TVP newSeqVarGC(size_t size, TVP *from, ...)
						// TVP newSetVarGC(size_t size, TVP *from, ...)
						// TVP newProductVarGC(size_t size, TVP *from, ...)
						// Therefore, 'from' is the second argument (at index 1)
						args.add(1, consReference(node));
					}
					else if(isMap(oldName) || isSetVarToGrow(oldName))
					{
						// The signature of 'newMapVarGC' is:
						// TVP newMapVarGC(size_t size, size_t expected_size, TVP *from, ...);
						// TVP newSetVarToGrowGC(size_t size, size_t expected_size, TVP *from, ...)
						// Therefore, 'from' is the third argument (at index 2)
						args.add(2, consReference(node));
					}
					else {
						args.add(consReference(node));
					}
				}
			}
		}
	}

	private SExpIR consReference(SExpIR exp)
	{
		// Expression is not associated with a memory address
		return CGenUtil.consCNull();
	}

	private boolean isFieldAccessor(String name)
	{
		return name.equals(CTransUtil.GET_FIELD) || name.equals(CTransUtil.GET_FIELD_PTR); 
	}
	
	private boolean isSetter(String name)
	{
		return name.equals(CTransUtil.SET_FIELD) || name.equals(CTransUtil.SET_FIELD_PTR);
	}
	
	private boolean isSeqOrSet(String name)
	{
		return name.equals(ColTrans.SEQ_VAR) || name.equals(ColTrans.SET_VAR);
	}
	
	private boolean isTupleExp(String name) {
	
		return name.equals(TupleTrans.TUPLE_EXP);
	}
	
	private boolean isMap(String name)
	{
		return name.equals(ColTrans.MAP_VAR);
	}
	
	private boolean isSetVarToGrow(String name)
	{
		return name.equals(CSetCompStrategy.NEW_SET_VAR_TO_GROW);
	}
	
	private boolean insideFieldInitializer(SExpIR exp)
	{
		AMethodDeclIR encMethod = exp.getAncestor(AMethodDeclIR.class);

		if (encMethod != null)
		{
			Object tag = encMethod.getTag();
			
			if(tag instanceof Vdm2cTag)
			{
				Vdm2cTag t = (Vdm2cTag) tag;
				return t.methodTags.contains(MethodTag.FieldInitializer);
			}
		}
		return false;
	}

}
