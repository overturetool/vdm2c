package org.overture.codegen.vdm2c.utils;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newIdentifier;

import java.util.List;

import org.apache.log4j.Logger;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.trans.let.LetBeStStrategy;
import org.overture.codegen.vdm2c.CForIterator;

public class CLetBeStStrategy extends LetBeStStrategy {

	protected Logger log = Logger.getLogger(this.getClass().getName());
	
	public CLetBeStStrategy(TransAssistantIR transformationAssistant, SExpIR suchThat, STypeIR setSeqType,
			ILanguageIterator langIterator, ITempVarGen tempGen, IterationVarPrefixes iteVarPrefixes) {
		super(transformationAssistant, suchThat, setSeqType, langIterator, tempGen, iteVarPrefixes);
	}
	
	@Override
	public SExpIR getForLoopCond(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
			throws AnalysisException
	{
		String iteratorName;
		
		if(langIterator instanceof CForIterator)
		{
			iteratorName = ((CForIterator) langIterator).getIteratorName();
		}
		else
		{
			log.error("Expected CForIterator at this point");
			return null;
		}
		
		SExpIR left = newApply("vdmLessThan", newIdentifier(iteratorName, null), newApply("vdmSetCard", newIdentifier(setVar.getName(), null)));
		SExpIR right = transAssist.consBoolCheck(successVarName, true);

		return CTransUtil.newApply("toBool", transAssist.consAndExp(left, right));
	}


}
