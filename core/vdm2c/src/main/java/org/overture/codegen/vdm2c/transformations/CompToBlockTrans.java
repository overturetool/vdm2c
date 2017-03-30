package org.overture.codegen.vdm2c.transformations;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SMultipleBindIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.ABoolLiteralExpIR;
import org.overture.codegen.ir.expressions.ACompSeqExpIR;
import org.overture.codegen.ir.expressions.ACompSetExpIR;
import org.overture.codegen.ir.expressions.AEnumSeqExpIR;
import org.overture.codegen.ir.expressions.AEnumSetExpIR;
import org.overture.codegen.ir.expressions.AForAllQuantifierExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.ALetBeStExpIR;
import org.overture.codegen.ir.patterns.ASetMultipleBindIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.SSetTypeIR;
import org.overture.codegen.ir.utils.AHeaderLetBeStIR;
import org.overture.codegen.trans.Exp2StmTrans;
import org.overture.codegen.trans.Exp2StmVarPrefixes;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.comp.ComplexCompStrategy;
import org.overture.codegen.trans.comp.SeqCompStrategy;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.trans.quantifier.Exists1CounterData;
import org.overture.codegen.trans.quantifier.OrdinaryQuantifier;
import org.overture.codegen.trans.quantifier.OrdinaryQuantifierStrategy;
import org.overture.codegen.vdm2c.utils.CLetBeStStrategy;
import org.overture.codegen.vdm2c.utils.COrdinaryQuantifierStrategy;
import org.overture.codegen.vdm2c.utils.CSeqCompStrategy;
import org.overture.codegen.vdm2c.utils.CSetCompStrategy;

/**
 * This must only be used before any C nodes are introduced. It runs of the base DepthFirst Visitor
 * 
 * @author kel
 */
public class CompToBlockTrans extends Exp2StmTrans
{

	public CompToBlockTrans(IterationVarPrefixes iteVarPrefixes,
			TransAssistantIR transAssistant, Exists1CounterData counterData,
			ILanguageIterator langIterator, Exp2StmVarPrefixes prefixes)
	{
		super(iteVarPrefixes, transAssistant, counterData, langIterator, prefixes);
	}
	
	@Override
	public void caseAForAllQuantifierExpIR(AForAllQuantifierExpIR node) throws AnalysisException {

		SStmIR enclosingStm = transAssistant.getEnclosingStm(node, "forall expression");

		SExpIR predicate = node.getPredicate();
		ITempVarGen tempVarNameGen = transAssistant.getInfo().getTempVarNameGen();
		String var = tempVarNameGen.nextVarName(prefixes.forAll());

		OrdinaryQuantifierStrategy strategy = new COrdinaryQuantifierStrategy(transAssistant, predicate, var, OrdinaryQuantifier.FORALL, langIterator, tempVarNameGen, iteVarPrefixes);

		List<SMultipleBindIR> multipleSetBinds = filterBindList(node, node.getBindList());

		ABlockStmIR block = transAssistant.consComplexCompIterationBlock(multipleSetBinds, tempVarNameGen, strategy, iteVarPrefixes);

		if (multipleSetBinds.isEmpty())
		{
			ABoolLiteralExpIR forAllResult = transAssistant.getInfo().getExpAssistant().consBoolLiteral(true);
			transAssistant.replaceNodeWith(node, forAllResult);
		} else
		{
			AIdentifierVarExpIR forAllResult = new AIdentifierVarExpIR();
			forAllResult.setIsLocal(true);
			forAllResult.setType(new ABoolBasicTypeIR());
			forAllResult.setName(var);

			transform(enclosingStm, block, forAllResult, node);
			block.apply(this);
		}
	}
	
	@Override
	public void caseALetBeStExpIR(ALetBeStExpIR node) throws AnalysisException {

		SStmIR enclosingStm = transAssistant.getEnclosingStm(node, "let be st expressions");

		AHeaderLetBeStIR header = node.getHeader();

		if (!(header.getBinding() instanceof ASetMultipleBindIR))
		{
			transAssistant.getInfo().addTransformationWarning(node.getHeader().getBinding(), "This transformation only works for 'let be st' "
					+ "expressions with with multiple set binds and not multiple type binds in '"
					+ this.getClass().getSimpleName() + "'");
			return;
		}

		ASetMultipleBindIR binding = (ASetMultipleBindIR) header.getBinding();
		SExpIR suchThat = header.getSuchThat();
		SSetTypeIR setType = transAssistant.getSetTypeCloned(binding.getSet());
		ITempVarGen tempVarNameGen = transAssistant.getInfo().getTempVarNameGen();

		CLetBeStStrategy strategy = new CLetBeStStrategy(transAssistant, suchThat, setType, langIterator, tempVarNameGen, iteVarPrefixes);

		ABlockStmIR outerBlock = new ABlockStmIR();

		SExpIR letBeStResult = null;

		if (transAssistant.hasEmptySet(binding))
		{
			transAssistant.cleanUpBinding(binding);
			letBeStResult = transAssistant.getInfo().getExpAssistant().consUndefinedExp();
		} else
		{
			String var = tempVarNameGen.nextVarName(prefixes.letBeSt());
			SExpIR value = node.getValue();

			AVarDeclIR resultDecl = transAssistant.consDecl(var, value.getType().clone(), transAssistant.getInfo().getExpAssistant().consUndefinedExp());
			outerBlock.getLocalDefs().add(resultDecl);

			AAssignToExpStmIR setLetBeStResult = new AAssignToExpStmIR();
			setLetBeStResult.setTarget(transAssistant.getInfo().getExpAssistant().consIdVar(var, value.getType().clone()));
			setLetBeStResult.setExp(value);
			outerBlock.getStatements().add(setLetBeStResult);

			AIdentifierVarExpIR varExpResult = new AIdentifierVarExpIR();
			varExpResult.setType(value.getType().clone());
			varExpResult.setIsLocal(true);
			varExpResult.setName(var);
			letBeStResult = varExpResult;
		}

		// Replace the let be st expression with the result expression
		transAssistant.replaceNodeWith(node, letBeStResult);

		LinkedList<SPatternIR> patterns = binding.getPatterns();
		ABlockStmIR block = transAssistant.consIterationBlock(patterns, binding.getSet(), tempVarNameGen, strategy, iteVarPrefixes);
		outerBlock.getStatements().addFirst(block);

		// Replace the enclosing statement with the transformation
		transAssistant.replaceNodeWith(enclosingStm, outerBlock);

		// And make sure to have the enclosing statement in the transformed tree
		outerBlock.getStatements().add(enclosingStm);
		outerBlock.apply(this);

		outerBlock.setScoped(transAssistant.getInfo().getStmAssistant().isScoped(outerBlock));
	}

	@Override
	public void caseACompSetExpIR(ACompSetExpIR node) throws AnalysisException
	{
		SStmIR enclosingStm = transAssistant.getEnclosingStm(node, "set comprehension");

		SExpIR first = node.getFirst();
		SExpIR predicate = node.getPredicate();
		STypeIR type = node.getType();
		ITempVarGen tempVarNameGen = transAssistant.getInfo().getTempVarNameGen();
		String var = tempVarNameGen.nextVarName(prefixes.setComp());

		ComplexCompStrategy strategy = new CSetCompStrategy(transAssistant, first, predicate, var, type, langIterator, tempVarNameGen, iteVarPrefixes);

		List<SMultipleBindIR> bindings = filterBindList(node, node.getBindings());
		ABlockStmIR block = transAssistant.consComplexCompIterationBlock(bindings, tempVarNameGen, strategy, iteVarPrefixes);

		if (block.getStatements().isEmpty())
		{
			// In case the block has no statements the result of the set comprehension is the empty set
			AEnumSetExpIR emptySet = new AEnumSetExpIR();
			emptySet.setType(type.clone());

			// Replace the set comprehension with the empty set
			transAssistant.replaceNodeWith(node, emptySet);
		} else
		{
			replaceCompWithTransformation(enclosingStm, block, type, var, node);
		}

		block.apply(this);
	}
	
	@Override
	public void caseACompSeqExpIR(ACompSeqExpIR node) throws AnalysisException {

		SStmIR enclosingStm = transAssistant.getEnclosingStm(node, "sequence comprehension");

		SExpIR first = node.getFirst();
		SExpIR predicate = node.getPredicate();
		STypeIR type = node.getType();
		ITempVarGen tempVarNameGen = transAssistant.getInfo().getTempVarNameGen();
		String var = tempVarNameGen.nextVarName(prefixes.seqComp());

		SeqCompStrategy strategy = new CSeqCompStrategy(transAssistant, first, predicate, var, type, langIterator, tempVarNameGen, iteVarPrefixes);

		if (transAssistant.isEmptySetSeq(node.getSetSeq()))
		{
			// In case the block has no statements the result of the sequence comprehension is the empty sequence
			AEnumSeqExpIR emptySeq = new AEnumSeqExpIR();
			emptySeq.setType(type.clone());

			// Replace the sequence comprehension with the empty sequence
			transAssistant.replaceNodeWith(node, emptySeq);
		} else
		{
			LinkedList<SPatternIR> patterns = new LinkedList<SPatternIR>();

			if (node.getSetBind() != null)
			{
				patterns.add(node.getSetBind().getPattern().clone());
			} else
			{
				patterns.add(node.getSeqBind().getPattern().clone());
			}

			ABlockStmIR block = transAssistant.consIterationBlock(patterns, node.getSetSeq(), transAssistant.getInfo().getTempVarNameGen(), strategy, iteVarPrefixes);

			replaceCompWithTransformation(enclosingStm, block, type, var, node);

			block.apply(this);
		}
	}

}
