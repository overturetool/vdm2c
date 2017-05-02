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
import org.overture.codegen.ir.expressions.AMapletExpIR;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.comp.MapCompStrategy;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.vdm2c.ColTrans;

public class CMapCompStrategy extends MapCompStrategy {

	public CMapCompStrategy(TransAssistantIR transformationAssitant, AMapletExpIR first, SExpIR predicate, String var,
			STypeIR compType, ILanguageIterator langIterator, ITempVarGen tempGen,
			IterationVarPrefixes iteVarPrefixes) {
		super(transformationAssitant, first, predicate, var, compType, langIterator, tempGen, iteVarPrefixes);
	}

	@Override
	protected SExpIR getEmptyCollection()
	{
		return CTransUtil.newApply(ColTrans.MAP_VAR, CTransUtil.consCInt("0"), CTransUtil.consCInt("5"));
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
		AIdentifierVarExpIR mapCompRes = new AIdentifierVarExpIR();
		mapCompRes.setType(compType.clone());
		mapCompRes.setName(idPattern.getName());
		mapCompRes.setIsLambda(false);
		mapCompRes.setIsLocal(true);

		SExpIR seqAdd = newApply("vdmMapGrow", mapCompRes, first.getLeft(), first.getRight());
		
		return consConditionalAdd(mapCompRes, CTransUtil.toStm(seqAdd));
	}

	@Override
	public List<SStmIR> getPostOuterBlockStms(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns)
	{
		AIdentifierVarExpIR mapCompRes = new AIdentifierVarExpIR();
		mapCompRes.setType(compType.clone());
		mapCompRes.setName(idPattern.getName());
		mapCompRes.setIsLambda(false);
		mapCompRes.setIsLocal(true);

		SExpIR setAdd = newApply("vdmMapFit", mapCompRes);
		return Collections.singletonList(toStm(setAdd));
	}
}
