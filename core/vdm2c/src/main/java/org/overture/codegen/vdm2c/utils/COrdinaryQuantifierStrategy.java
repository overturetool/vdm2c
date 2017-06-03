package org.overture.codegen.vdm2c.utils;

import java.util.List;

import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.trans.quantifier.OrdinaryQuantifier;
import org.overture.codegen.trans.quantifier.OrdinaryQuantifierStrategy;

public class COrdinaryQuantifierStrategy extends OrdinaryQuantifierStrategy {

	public COrdinaryQuantifierStrategy(TransAssistantIR transformationAssistant, SExpIR predicate, String resultVarName,
			OrdinaryQuantifier quantifier, ILanguageIterator langIterator, ITempVarGen tempGen,
			IterationVarPrefixes iteVarPrefixes) {
		super(transformationAssistant, predicate, resultVarName, quantifier, langIterator, tempGen, iteVarPrefixes);
	}
	
	@Override
	public SExpIR getForLoopCond(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
			throws AnalysisException
	{
		SExpIR left = langIterator.getForLoopCond(setVar, patterns, pattern);
		
		SExpIR right = transAssist.consBoolCheck(resultVarName, quantifier == OrdinaryQuantifier.EXISTS);

		SExpIR andExp = transAssist.consAndExp(left, right);
		
		return CTransUtil.newApply("toBool", andExp);
	}

}
