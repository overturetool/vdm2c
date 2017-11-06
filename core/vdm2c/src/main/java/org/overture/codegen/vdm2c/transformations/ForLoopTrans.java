package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.createIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newArrayIndex;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newCExp;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newDeclarationAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newExternalType;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newIntLiteralExp;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newMacroApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newPtrDeref;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newTvpType;
import static org.overture.codegen.vdm2c.utils.CTransUtil.toStm;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.AForAllStmIR;
import org.overture.codegen.ir.statements.AForIndexStmIR;
import org.overture.codegen.ir.statements.AWhileStmIR;
import org.overture.codegen.ir.types.ANatNumericBasicTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class ForLoopTrans extends DepthFirstAnalysisCAdaptor
{
	public TransAssistantIR assist;
	final static String retPrefix = "forLoop_";

	public ForLoopTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	String getNewName()
	{
		return assist.getInfo().getTempVarNameGen().nextVarName(retPrefix);
	}

	@Override
	public void caseAForIndexStmIR(AForIndexStmIR node)
			throws AnalysisException
	{
		super.caseAForIndexStmIR(node);
		
		String bindName = node.getVar();

		if (node.getBy() == null)
		{
			AApplyExpIR newInt = new AApplyExpIR();
			newInt.setType(new ANatNumericBasicTypeIR());
			newInt.getArgs().add(newIntLiteralExp(1));
			newInt.setRoot(createIdentifier(LiteralInstantiationRewriteTrans.NEW_INT, node.getSourceNode()));
			node.setBy(newInt);
		}

		ABlockStmIR replBlock = new ABlockStmIR();
		replBlock.setScoped(true);

		String indexName = getNewName();
		replBlock.getLocalDefs().add(newDeclarationAssignment(indexName, newExternalType("int"), newApply("toInteger", node.getFrom().clone()), null));

		SNumericBinaryExpIR less;

		AWhileStmIR loop = new AWhileStmIR();
		replBlock.getStatements().add(loop);

		ABlockStmIR whileBlock = new ABlockStmIR();
		whileBlock.setScoped(true);
		loop.setBody(whileBlock);
		whileBlock.getLocalDefs().add(newDeclarationAssignment(bindName, newTvpType(), newApply(LiteralInstantiationRewriteTrans.NEW_INT, createIdentifier(indexName, null)), null));
		whileBlock.getStatements().add(node.getBody());
		whileBlock.getStatements().add(toStm(ValueSemantics.free(bindName, node.getSourceNode())));

		APlusNumericBinaryExpIR pp = new APlusNumericBinaryExpIR();
		pp.setLeft(createIdentifier(indexName, null));

		pp.setRight(newApply("toInteger", node.getBy().clone()));
		if (isPositiveInt(node.getBy()))
		{
			less = new ALessEqualNumericBinaryExpIR();

		} else
		{
			less = new AGreaterEqualNumericBinaryExpIR();
		}
		
		less.setLeft(newIdentifier(indexName, null));
		less.setRight(newApply("toInteger", node.getTo()));
		loop.setExp(newCExp(less));

		whileBlock.getStatements().add(newAssignment(createIdentifier(indexName, null), pp));

		assist.replaceNodeWith(node, replBlock);
	}

	private boolean isPositiveInt(SExpIR by) {
		
		if(by instanceof AApplyExpIR)
		{
			AApplyExpIR apply = (AApplyExpIR) by;
			
			if(apply.getRoot() instanceof AIdentifierVarExpIR)
			{
				AIdentifierVarExpIR id = (AIdentifierVarExpIR) apply.getRoot();
				
				return !id.getName().equals(NumericTrans.VDM_MINUS);
			}
		}
		
		return false;
	}

	@Override
	public void caseAForAllStmIR(AForAllStmIR node) throws AnalysisException
	{
		super.caseAForAllStmIR(node);
		rewritetoCollectionLoop(node, node.getExp(), node.getPattern(), node.getBody());
	}

	void rewritetoCollectionLoop(INode source, SExpIR set,
			SPatternIR sPatternIR, SStmIR body)
	{
		String bindName = null;
		if (sPatternIR instanceof AIdentifierPatternIR)
		{
			bindName = ((AIdentifierPatternIR) sPatternIR).getName();
		}

		ABlockStmIR replBlock = new ABlockStmIR();
		replBlock.setScoped(true);

		String setName = getNewName();
		String indexName = getNewName();
		replBlock.getLocalDefs().add(newDeclarationAssignment(setName, newTvpType(), set, set.getSourceNode()));

		AExternalExpIR zero = new AExternalExpIR();
		zero.setTargetLangExp("0");
		zero.setType(newExternalType("int"));

		replBlock.getLocalDefs().add(newDeclarationAssignment(indexName, newExternalType("int"), zero, null));
		replBlock.getStatements().add(toStm(newMacroApply("ASSERT_CHECK_COLLECTION", newIdentifier(setName, set.getSourceNode()))));
		String colName = getNewName();
		replBlock.getStatements().add(toStm(newMacroApply("UNWRAP_COLLECTION", newIdentifier(colName, set.getSourceNode()), newIdentifier(setName, set.getSourceNode()))));

		SNumericBinaryExpIR less = new ALessNumericBinaryExpIR();
		less.setLeft(newIdentifier(indexName, null));
		less.setRight(newPtrDeref(newIdentifier(colName, null), newIdentifier("size", null)));

		AWhileStmIR loop = new AWhileStmIR();
		replBlock.getStatements().add(loop);
		loop.setExp(newCExp(less));

		ABlockStmIR whileBlock = new ABlockStmIR();
		whileBlock.setScoped(true);
		loop.setBody(whileBlock);
		whileBlock.getLocalDefs().add(newDeclarationAssignment(bindName, newTvpType(), newArrayIndex(newPtrDeref(createIdentifier(colName, null), createIdentifier("value", null)), createIdentifier(indexName, null)), null));
		whileBlock.getStatements().add(body);

		APlusNumericBinaryExpIR pp = new APlusNumericBinaryExpIR();
		pp.setLeft(createIdentifier(indexName, null));
		pp.setRight(newIntLiteralExp(1));

		whileBlock.getStatements().add(newAssignment(createIdentifier(indexName, null), pp));

		assist.replaceNodeWith(source, replBlock);
	}

}
