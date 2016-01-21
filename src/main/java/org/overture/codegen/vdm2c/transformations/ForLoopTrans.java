package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.*;

import org.overture.ast.expressions.ALessNumericBinaryExp;
import org.overture.ast.expressions.APlusPlusBinaryExp;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.SClassDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ALessNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.APlusNumericBinaryExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AForAllStmCG;
import org.overture.codegen.cgast.statements.AForIndexStmCG;
import org.overture.codegen.cgast.statements.AForLoopStmCG;
import org.overture.codegen.cgast.statements.AWhileStmCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class ForLoopTrans extends DepthFirstAnalysisAdaptor
{
	public TransAssistantCG assist;
	final static String retPrefix = "forLoop_";

	public ForLoopTrans(TransAssistantCG assist)
	{
		this.assist = assist;
	}

	String getNewName()
	{
		return assist.getInfo().getTempVarNameGen().nextVarName(retPrefix);
	}

	@Override
	public void caseAForIndexStmCG(AForIndexStmCG node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseAForIndexStmCG(node);
//		node.get
	}

	@Override
	public void caseAForAllStmCG(AForAllStmCG node) throws AnalysisException
	{
		super.caseAForAllStmCG(node);
		rewritetoCollectionLoop(node,node.getExp(),node.getPattern(),node.getBody());
	}

	

	void rewritetoCollectionLoop(INode source, SExpCG set, SPatternCG sPatternCG,SStmCG body)
	{
		String bindName = null;
		if (sPatternCG instanceof AIdentifierPatternCG)
			bindName = ((AIdentifierPatternCG) sPatternCG).getName();

		ABlockStmCG replBlock = new ABlockStmCG();
		replBlock.setScoped(true);

		String setName = getNewName();
		String indexName = getNewName();
		replBlock.getLocalDefs().add(newDeclarationAssignment(setName, newTvpType(), set, SourceNode.copy(set.getSourceNode())));
		replBlock.getLocalDefs().add(newDeclarationAssignment(indexName, newExternalType("int"), createIdentifier("0", null), null));
		replBlock.getStatements().add(toStm(newMacroApply("ASSERT_CHECK_COLLECTION", newIdentifier(setName, SourceNode.copy(set.getSourceNode())))));
		String colName = getNewName();
		replBlock.getStatements().add(toStm(newMacroApply("UNWRAP_COLLECTION", newIdentifier(colName, SourceNode.copy(set.getSourceNode())), newIdentifier(setName, SourceNode.copy(set.getSourceNode())))));

		ALessNumericBinaryExpCG less = new ALessNumericBinaryExpCG();
		less.setLeft(newIdentifier(indexName, null));
		less.setRight(newPtrDeref(newIdentifier(colName, null), newIdentifier("size", null)));

		AWhileStmCG loop = new AWhileStmCG();
		replBlock.getStatements().add(loop);
		loop.setExp(newCExp(less));

		ABlockStmCG whileBlock = new ABlockStmCG();
		whileBlock.setScoped(true);
		loop.setBody(whileBlock);
		whileBlock.getLocalDefs().add(newDeclarationAssignment(bindName, newTvpType(), newArrayIndex(newPtrDeref(createIdentifier(colName, null), createIdentifier("value", null)), createIdentifier(indexName, null)), null));
		whileBlock.getStatements().add(body);
		
		APlusNumericBinaryExpCG pp = new APlusNumericBinaryExpCG();
		pp.setLeft(createIdentifier(indexName, null));
		pp.setRight(createIdentifier("1", null));
		
		whileBlock.getStatements().add(newAssignment(createIdentifier(indexName, null), pp));
		
		assist.replaceNodeWith(source, replBlock);
	}

}
