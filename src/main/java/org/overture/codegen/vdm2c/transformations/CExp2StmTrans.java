package org.overture.codegen.vdm2c.transformations;

import java.util.List;

import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.expressions.ACompSetExpIR;
import org.overture.codegen.ir.expressions.AEnumSetExpIR;
import org.overture.codegen.ir.patterns.ASetMultipleBindIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.trans.Exp2StmTrans;
import org.overture.codegen.trans.Exp2StmVarPrefixes;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.comp.ComplexCompStrategy;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.trans.quantifier.Exists1CounterData;
import org.overture.codegen.vdm2c.utils.CSetCompStrategy;

/**
 * This must only be used before any C nodes are introduced. It runs of the base DepthFirst Visitor
 * 
 * @author kel
 */
public class CExp2StmTrans extends Exp2StmTrans
{

	public CExp2StmTrans(IterationVarPrefixes iteVarPrefixes,
			TransAssistantIR transAssistant, Exists1CounterData counterData,
			ILanguageIterator langIterator, Exp2StmVarPrefixes prefixes)
	{
		super(iteVarPrefixes, transAssistant, counterData, langIterator, prefixes);
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

		List<ASetMultipleBindIR> bindings = filterBindList(node, node.getBindings());
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

}
