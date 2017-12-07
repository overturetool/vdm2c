package org.overture.codegen.vdm2c.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.*;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AExternalExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.APlainCallStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.statements.ASkipStmIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AUnknownTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.CForIterator;
import org.overture.codegen.vdm2c.ColTrans;
import org.overture.codegen.vdm2c.TupleTrans;
import org.overture.codegen.vdm2c.Vdm2cTag;
import org.overture.codegen.vdm2c.Vdm2cTag.MethodTag;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;
import org.overture.codegen.vdm2c.extast.statements.ALocalVariableDeclarationStmIR;
import org.overture.codegen.vdm2c.tags.CTags;
import org.overture.codegen.vdm2c.utils.CGenUtil;
import org.overture.codegen.vdm2c.utils.CLetBeStStrategy;
import org.overture.codegen.vdm2c.utils.CSetCompStrategy;
import org.overture.codegen.vdm2c.utils.CTransUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;

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
		gcNames.put(IsCheckTrans.VDM_IS_REAL, "isRealGC");
		gcNames.put(IsCheckTrans.VDM_IS_BOOL, "isBoolGC");
		gcNames.put(IsCheckTrans.VDM_IS_RAT, "isRatGC");
		gcNames.put(IsCheckTrans.VDM_IS_CHAR, "isCharGC");
		gcNames.put(IsCheckTrans.VDM_IS_TOKEN, "isTokenGC");
		gcNames.put(IsCheckTrans.VDM_IS, "isGC");

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
		gcNames.put(ColTrans.MAP_ITERATE, "vdmMapIterateGC");
		gcNames.put(ColTrans.MAP_COMP, "vdmMapComposeGC");

		// OO checks
		gcNames.put(OOCheckTrans.IS_OF_BASE_CLASS, "isOfBaseClassGC");
		gcNames.put(OOCheckTrans.IS_OF_CLASS, "isOfClassGC");
		gcNames.put(OOCheckTrans.SAME_BASE_CLASS, "sameBaseClassGC");
		gcNames.put(OOCheckTrans.SAME_CLASS, "sameClassGC");

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
		
		changeToGcCall(node, node.getArgs(), node.getRoot());
	}
	
	@Override
	public void caseAApplyExpIR(AApplyExpIR node) throws AnalysisException
	{
		super.caseAApplyExpIR(node);

		changeToGcCall(node, node.getArgs(), node.getRoot());
	}

	@Override
	public void caseAReturnStmIR(AReturnStmIR node) throws AnalysisException {
		super.caseAReturnStmIR(node);

		if(isInsideFieldInitializer(node))
		{
			SExpIR exp = node.getExp();
			SExpIR cloned = ValueSemantics.forceClone(exp.clone());
			assist.replaceNodeWith(exp, cloned);
		}
	}

	@Override
	public void caseAMethodDeclIR(AMethodDeclIR node) throws AnalysisException {
		super.caseAMethodDeclIR(node);

		if(!node.getIsConstructor())
		{
			return;
		}

		ADefaultClassDeclIR clazz = node.getAncestor(ADefaultClassDeclIR.class);

		if(clazz != null)
		{
			AExternalTypeIR tvp = new AExternalTypeIR();
			tvp.setName("TVP");

			String fromName = "from";
			AIdentifierPatternIR fromId = new AIdentifierPatternIR();
			fromId.setName("*" + fromName);

			AFormalParamLocalParamIR from = new AFormalParamLocalParamIR();
			from.setType(tvp);
			from.setPattern(fromId);

			AIdentifierPatternIR tmpId = new AIdentifierPatternIR();
			String tmpName = "tmp";
			tmpId.setName(tmpName);

			AIdentifierVarExpIR root = new AIdentifierVarExpIR();
			root.setName(node.getName());
			root.setType(node.getMethodType().clone());

			AApplyExpIR callToRegularCtor = new AApplyExpIR();
			callToRegularCtor.setType(node.getMethodType().getResult().clone());
			callToRegularCtor.setRoot(root);

			for(AFormalParamLocalParamIR param : node.getFormalParams())
			{
				STypeIR type = param.getType();
				SPatternIR pattern = param.getPattern();

				if(pattern instanceof AIdentifierPatternIR)
				{
					AIdentifierPatternIR id = (AIdentifierPatternIR) pattern;

					AIdentifierVarExpIR arg = new AIdentifierVarExpIR();
					arg.setType(type.clone());
					arg.setName(id.getName());

					callToRegularCtor.getArgs().add(arg);
				}
				else
				{
					logger.error("Expected formal paramater pattern to be an identifier pattern at this point. Got: " + pattern);
				}
			}

			AVarDeclIR tmp = assist.getInfo().getDeclAssistant().consLocalVarDecl(tvp.clone(), tmpId, callToRegularCtor);

			ALocalVariableDeclarationStmIR declStm = new ALocalVariableDeclarationStmIR();
			declStm.setDecleration(tmp);

			AIdentifierVarExpIR tmpArg = new AIdentifierVarExpIR();
			tmpArg.setType(tmp.getType().clone());
			tmpArg.setName(tmpName);

			AIdentifierVarExpIR fromArg = new AIdentifierVarExpIR();
			fromArg.setType(tvp.clone());

			AIdentifierVarExpIR addNodeRoot = new AIdentifierVarExpIR();
			addNodeRoot.setName("add_allocd_mem_node");
			addNodeRoot.setType(new AUnknownTypeIR());

			AMacroApplyExpIR addNode = new AMacroApplyExpIR();
			addNode.setRoot(addNodeRoot);
			addNode.getArgs().add(tmpArg);

			AReturnStmIR retTmp = new AReturnStmIR();
			retTmp.setExp(tmpArg.clone());

			ABlockStmIR replBlock = new ABlockStmIR();
			replBlock.getStatements().add(declStm);
			replBlock.getStatements().add(CTransUtil.toStm(addNode));
			replBlock.getStatements().add(retTmp);

			AMethodDeclIR gcCtor = node.clone();
			gcCtor.setTag(CTags.GC_CONSTRUCTOR);
			gcCtor.setName(node.getName() + "GC");
			gcCtor.setBody(replBlock);

			for(int i = 0; i < clazz.getMethods().size(); i++)
			{
				AMethodDeclIR m = clazz.getMethods().get(i);

				if(m == node)
				{
					clazz.getMethods().add(i + 1, gcCtor);
					break;
				}
			}
		}
	}

	private void changeToGcCall(SExpIR node, LinkedList<SExpIR> args,
								SExpIR root)
	{
		// Assignments to static instance variables require special treatment. See
		// https://github.com/overturetool/vdm2c/issues/122#issuecomment-349663826
		if(node.getTag() == CTags.EXP_ASSIGNED_TO_STATIC_FIELD)
		{
			return;
		}

		if (root instanceof AIdentifierVarExpIR)
		{
			AIdentifierVarExpIR id = (AIdentifierVarExpIR) root;

			String oldName = id.getName();
			
			String val = gcNames.get(oldName);

			if (val != null)
			{
				id.setName(val);
			}
			else if(node.getTag() == CTags.CONSTRUCTOR_CALL)
			{
				if(node instanceof AApplyExpIR)
				{
					AApplyExpIR apply = (AApplyExpIR) node;

					if(apply.getRoot() instanceof AIdentifierVarExpIR)
					{
						AIdentifierVarExpIR var = (AIdentifierVarExpIR) apply.getRoot();
						var.setName(var.getName() + "GC");
					}
					else
					{
						logger.error("Expected root to be a variable expression at this point. Got: " + apply.getRoot());
					}
				}
				else
				{
					logger.error("Expected constructor call to be an apply expression at this point. Got: " + node);
				}
			}
		}
	}

	private boolean isInsideFieldInitializer(INode node) {
		AMethodDeclIR encMethod = node.getAncestor(AMethodDeclIR.class);

		if (encMethod != null) {
			Object tag = encMethod.getTag();

			if (tag instanceof Vdm2cTag) {
				Vdm2cTag t = (Vdm2cTag) tag;
				return t.methodTags.contains(MethodTag.FieldInitializer);
			}
		}

		return false;
	}
}
