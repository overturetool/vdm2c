package org.overture.codegen.vdm2c.transformations;

import java.util.HashMap;
import java.util.LinkedList;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AExternalExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.CForIterator;
import org.overture.codegen.vdm2c.ColTrans;
import org.overture.codegen.vdm2c.Vdm2cTag;
import org.overture.codegen.vdm2c.Vdm2cTag.MethodTag;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;
import org.overture.codegen.vdm2c.utils.CLetBeStStrategy;
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
		
		// Collections
		gcNames.put(ColTrans.SEQ_VAR, "newSeqVarGC");
		gcNames.put(ColTrans.SET_VAR, "newSetVarGC");
		
		// Sequence operations
		gcNames.put(ColTrans.SEQ_TAIL, "vdmSeqTlGC");
		gcNames.put(ColTrans.SEQ_LEN, "vdmSeqLenGC");
		gcNames.put(ColTrans.SEQ_HEAD, "vdmSeqHdGC");
		gcNames.put(ColTrans.SEQ_CONC, "vdmSeqConcGC");
		gcNames.put(ColTrans.SEQ_REVERSE, "vdmSeqReverseGC");
		gcNames.put(ColTrans.SEQ_INDEX, "vdmSeqIndexGC");
		
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
		
		// Copying
		gcNames.put(ValueSemantics.VDM_CLONE, "vdmCloneGC");
		
		// Accessors
		gcNames.put(CTransUtil.GET_FIELD, "GET_FIELD_GC");
		gcNames.put(CTransUtil.GET_FIELD_PTR, "GET_FIELD_PTR_GC");
		
		// Setters
		gcNames.put(CTransUtil.SET_FIELD, "SET_FIELD_GC");
		gcNames.put(CTransUtil.SET_FIELD_PTR, "SET_FIELD_PTR_GC");
		
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
					if (isSeqOrSetEnum(oldName)) {
						// The signatures of 'newSeqVarGC' and newSetVarGC are:
						// TVP newSeqVarGC(size_t size, TVP *from, ...)
						// TVP newSetVarGC(size_t size, TVP *from, ...)
						// Therefore, 'from' is the second argument (at index 1)
						args.add(1, consReference(node));
					} else {
						args.add(consReference(node));
					}
				}
			}
		}
	}

	private SExpIR consReference(SExpIR exp)
	{
		INode parent = exp.parent();
		
		if (parent instanceof AVarDeclIR)
		{
			SPatternIR pat = ((AVarDeclIR) parent).getPattern();

			if (pat instanceof AIdentifierPatternIR)
			{
				String name = ((AIdentifierPatternIR) pat).getName();
				AExternalExpIR reference = new AExternalExpIR();
				reference.setSourceNode(pat.getSourceNode());
				String referencePrefix = findReferencePrefix(exp);
				reference.setTargetLangExp(referencePrefix + name);
				
				return reference;
			}
			else
			{
				logger.error("Expected identifier pattern at this point.");
			}
		}

		// Expression is not associated with a memory address
		return assist.getInfo().getExpAssistant().consNullExp();
	}
	
	private String findReferencePrefix(SExpIR exp)
	{
		if(exp instanceof AMacroApplyExpIR)
		{
			AMacroApplyExpIR macro = (AMacroApplyExpIR) exp;
			
			SExpIR root = macro.getRoot();
			if(root instanceof AIdentifierVarExpIR)
			{
				if(((AIdentifierVarExpIR) root).getName().equals(SELF_GC))
				{
					// No prefix needed - the SELF macro only needs the name
					return "";
				}
			}
		}

		// The 'address-of' operator
		return "&";
	}

	private boolean isFieldAccessor(String name)
	{
		return name.equals(CTransUtil.GET_FIELD) || name.equals(CTransUtil.GET_FIELD_PTR); 
	}
	
	private boolean isSetter(String name)
	{
		return name.equals(CTransUtil.SET_FIELD) || name.equals(CTransUtil.SET_FIELD_PTR);
	}
	
	private boolean isSeqOrSetEnum(String name)
	{
		return name.equals(ColTrans.SEQ_VAR) || name.equals(ColTrans.SET_VAR);
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
