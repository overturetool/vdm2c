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

import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.expressions.ALessEqualNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.ALessNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.APlusNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.SNumericBinaryExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.AForAllStmIR;
import org.overture.codegen.ir.statements.AForIndexStmIR;
import org.overture.codegen.ir.statements.AWhileStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class ForLoopTrans extends DepthFirstAnalysisAdaptor
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
		String bindName = node.getVar();

		if (node.getBy() == null)
		{
			node.setBy(newIntLiteralExp(1));
		}

		ABlockStmIR replBlock = new ABlockStmIR();
		replBlock.setScoped(true);

		String indexName = getNewName();
		replBlock.getLocalDefs().add(newDeclarationAssignment(indexName, newExternalType("int"), newApply("toInteger", node.getFrom()), null));

		SNumericBinaryExpIR less = new ALessEqualNumericBinaryExpIR();
		less.setLeft(newIdentifier(indexName, null));
		less.setRight(newApply("toInteger", node.getTo()));

		AWhileStmIR loop = new AWhileStmIR();
		replBlock.getStatements().add(loop);
		loop.setExp(newCExp(less));

		ABlockStmIR whileBlock = new ABlockStmIR();
		whileBlock.setScoped(true);
		loop.setBody(whileBlock);
		whileBlock.getLocalDefs().add(newDeclarationAssignment(bindName, newTvpType(), newApply("newInt", createIdentifier(indexName, null)), null));
		whileBlock.getStatements().add(node.getBody());
		whileBlock.getStatements().add(toStm(newApply("vdmFree", createIdentifier(bindName, null))));

		APlusNumericBinaryExpIR pp = new APlusNumericBinaryExpIR();
		pp.setLeft(createIdentifier(indexName, null));
		pp.setRight(newApply("toInteger", node.getBy()));

		whileBlock.getStatements().add(newAssignment(createIdentifier(indexName, null), pp));

		assist.replaceNodeWith(node, replBlock);
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
		replBlock.getLocalDefs().add(newDeclarationAssignment(indexName, newExternalType("int"), createIdentifier("0", null), null));
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
