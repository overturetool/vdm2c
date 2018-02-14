package org.overture.codegen.vdm2c.utils;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.toStm;

import java.util.Collections;
import java.util.List;

import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.comp.SeqCompStrategy;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public class CSeqCompStrategy extends SeqCompStrategy {

	public static final String NEW_SEQ_VAR_TO_GROW = "newSeqVarToGrow";

	public CSeqCompStrategy(TransAssistantIR transformationAssitant, SExpIR first, SExpIR predicate, String var,
							STypeIR compType, ILanguageIterator langIterator, ITempVarGen tempGen,
							IterationVarPrefixes iteVarPrefixes) {
		super(transformationAssitant, first, predicate, var, compType, langIterator, tempGen, iteVarPrefixes);
	}

	
	@Override
	protected SExpIR getEmptyCollection()
	{
		// TODO: request
		// Framework change such
		// that this is inserted
		// after the set is
		// Evaluated such that a
		// useful estimate can
		// be made of the size
		return CTransUtil.newApply(NEW_SEQ_VAR_TO_GROW, CTransUtil.consCInt("0"), CTransUtil.consCInt("5"));
	}
	
	@Override
	public SExpIR getForLoopCond(AIdentifierVarExpIR setVar, List<SPatternIR> patterns, SPatternIR pattern)
			throws AnalysisException {
		
		return CTransUtil.newApply("toBool", super.getForLoopCond(setVar, patterns, pattern));
	}
	
	@Override
	protected List<SStmIR> getConditionalAdd(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
	{
		AIdentifierVarExpIR seqCompRes = new AIdentifierVarExpIR();
		seqCompRes.setType(compType.clone());
		seqCompRes.setName(idPattern.getName());
		seqCompRes.setIsLambda(false);
		seqCompRes.setIsLocal(true);

		SExpIR seqAdd = newApply("vdmSeqGrow", seqCompRes, first.clone());
		
		return consConditionalAdd(seqCompRes, CTransUtil.toStm(seqAdd));
	}

	@Override
	public List<SStmIR> getPostOuterBlockStms(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns)
	{
		AIdentifierVarExpIR setCompResult = new AIdentifierVarExpIR();
		setCompResult.setType(compType.clone());
		setCompResult.setName(idPattern.getName());
		setCompResult.setIsLambda(false);
		setCompResult.setIsLocal(true);

		SExpIR setAdd = newApply("vdmSeqFit", setCompResult);
		return Collections.singletonList(toStm(setAdd));
	}
}
