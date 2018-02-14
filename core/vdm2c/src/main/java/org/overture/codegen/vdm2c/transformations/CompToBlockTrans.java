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
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.ir.patterns.ASetMultipleBindIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.SSetTypeIR;
import org.overture.codegen.ir.utils.AHeaderLetBeStIR;
import org.overture.codegen.trans.Exp2StmTrans;
import org.overture.codegen.trans.Exp2StmVarPrefixes;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.comp.ComplexCompStrategy;
import org.overture.codegen.trans.comp.SeqCompStrategy;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.trans.quantifier.CounterData;
import org.overture.codegen.trans.quantifier.OrdinaryQuantifier;
import org.overture.codegen.trans.quantifier.OrdinaryQuantifierStrategy;
import org.overture.codegen.vdm2c.utils.*;

/**
 * This must only be used before any C nodes are introduced. It runs of the base DepthFirst Visitor
 * 
 * @author kel
 */
public class CompToBlockTrans extends Exp2StmTrans
{

	public CompToBlockTrans(IterationVarPrefixes iteVarPrefixes,
			TransAssistantIR transAssistant, CounterData counterData,
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
	public void caseAExistsQuantifierExpIR(AExistsQuantifierExpIR node)
			throws AnalysisException
	{
		SStmIR enclosingStm = transAssistant.getEnclosingStm(node, "exists expression");

		SExpIR predicate = node.getPredicate();
		ITempVarGen tempVarNameGen = transAssistant.getInfo().getTempVarNameGen();
		String var = tempVarNameGen.nextVarName(prefixes.exists());

		OrdinaryQuantifierStrategy strategy = new COrdinaryQuantifierStrategy(transAssistant, predicate, var, OrdinaryQuantifier.EXISTS, langIterator, tempVarNameGen, iteVarPrefixes);

		List<SMultipleBindIR> multipleSetBinds = filterBindList(node, node.getBindList());

		ABlockStmIR block = transAssistant.consComplexCompIterationBlock(multipleSetBinds, tempVarNameGen, strategy, iteVarPrefixes);

		if (multipleSetBinds.isEmpty())
		{
			ABoolLiteralExpIR existsResult = transAssistant.getInfo().getExpAssistant().consBoolLiteral(false);
			transAssistant.replaceNodeWith(node, existsResult);
		} else
		{
			AIdentifierVarExpIR existsResult = new AIdentifierVarExpIR();
			existsResult.setIsLocal(true);
			existsResult.setType(new ABoolBasicTypeIR());
			existsResult.setName(var);

			transform(enclosingStm, block, existsResult, node);
			block.apply(this);
		}
	}

	@Override
	public void caseAExists1QuantifierExpIR(AExists1QuantifierExpIR node) throws AnalysisException {

		SStmIR enclosingStm = transAssistant.getEnclosingStm(node, "exists1 expression");

		SExpIR predicate = node.getPredicate();
		ITempVarGen tempVarNameGen = transAssistant.getInfo().getTempVarNameGen();
		String var = tempVarNameGen.nextVarName(prefixes.exists1());

		CExists1QuantifierStrategy strategy = new CExists1QuantifierStrategy(transAssistant, predicate, var, langIterator, tempVarNameGen, iteVarPrefixes, counterData);

		List<SMultipleBindIR> multipleSetBinds = filterBindList(node, node.getBindList());

		ABlockStmIR block = transAssistant.consComplexCompIterationBlock(multipleSetBinds, tempVarNameGen, strategy, iteVarPrefixes);

		if (multipleSetBinds.isEmpty())
		{
			ABoolLiteralExpIR exists1Result = transAssistant.getInfo().getExpAssistant().consBoolLiteral(false);
			transAssistant.replaceNodeWith(node, exists1Result);
		} else
		{
			AIdentifierVarExpIR counter = new AIdentifierVarExpIR();
			counter.setType(new AIntNumericBasicTypeIR());
			counter.setIsLocal(true);
			counter.setName(var);

			AEqualsBinaryExpIR exists1Result = new AEqualsBinaryExpIR();
			exists1Result.setType(new ABoolBasicTypeIR());
			exists1Result.setLeft(counter);
			exists1Result.setRight(transAssistant.getInfo().getExpAssistant().consIntLiteral(1));

			transform(enclosingStm, block, exists1Result, node);
			block.apply(this);
		}
	}

	@Override
	public void caseAIotaExpIR(AIotaExpIR node) throws AnalysisException {

		SStmIR enclosingStm = transAssistant.getEnclosingStm(node, "iota expression");

		SExpIR predicate = node.getPredicate();
		ITempVarGen tempVarNameGen = transAssistant.getInfo().getTempVarNameGen();
		String resVarName = tempVarNameGen.nextVarName(prefixes.iota());
		String counterName = tempVarNameGen.nextVarName(prefixes.iotaCounter());

		CIotaStrategy strategy = new CIotaStrategy(transAssistant, predicate, resVarName, counterName, langIterator, tempVarNameGen, iteVarPrefixes, counterData);

		List<SMultipleBindIR> multipleSetBinds = filterBindList(node, node.getBindList());

		ABlockStmIR block = transAssistant.consComplexCompIterationBlock(multipleSetBinds, tempVarNameGen, strategy, iteVarPrefixes);

		if (multipleSetBinds.isEmpty())
		{
			transAssistant.replaceNodeWith(node, transAssistant.getInfo().getExpAssistant().consUndefinedExp());
		} else
		{
			AIdentifierVarExpIR iotaResult = new AIdentifierVarExpIR();
			iotaResult.setIsLocal(true);
			iotaResult.setType(node.getType().clone());
			iotaResult.setName(resVarName);

			transform(enclosingStm, block, iotaResult, node);
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
	
	@Override
	public void caseACompMapExpIR(ACompMapExpIR node) throws AnalysisException {

		SStmIR enclosingStm = transAssistant.getEnclosingStm(node, "map comprehension");

		AMapletExpIR first = node.getFirst();
		SExpIR predicate = node.getPredicate();
		STypeIR type = node.getType();
		ITempVarGen tempVarNameGen = transAssistant.getInfo().getTempVarNameGen();
		String var = tempVarNameGen.nextVarName(prefixes.mapComp());

		ComplexCompStrategy strategy = new CMapCompStrategy(transAssistant, first, predicate, var, type, langIterator,
				tempVarNameGen, iteVarPrefixes);

		List<SMultipleBindIR> bindings = filterBindList(node, node.getBindings());

		ABlockStmIR block = transAssistant.consComplexCompIterationBlock(bindings, tempVarNameGen, strategy,
				iteVarPrefixes);

		if (block.getStatements().isEmpty()) {
			// In case the block has no statements the result of the map
			// comprehension is the empty map
			AEnumMapExpIR emptyMap = new AEnumMapExpIR();
			emptyMap.setType(type.clone());

			// Replace the map comprehension with the empty map
			transAssistant.replaceNodeWith(node, emptyMap);
		} else {
			replaceCompWithTransformation(enclosingStm, block, type, var, node);
		}

		block.apply(this);
	}

}
