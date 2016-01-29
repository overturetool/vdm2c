package org.overture.codegen.vdm2c.utils;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.toStm;

import java.util.Collections;
import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.comp.SetCompStrategy;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public class CSetCompStrategy extends SetCompStrategy
{

	public CSetCompStrategy(TransAssistantCG transformationAssitant,
			SExpCG first, SExpCG predicate, String var, STypeCG compType,
			ILanguageIterator langIterator, ITempVarGen tempGen,
			IterationVarPrefixes iteVarPrefixes)
	{
		super(transformationAssitant, first, predicate, var, compType, langIterator, tempGen, iteVarPrefixes);
	}

	@Override
	protected SExpCG getEmptyCollection()
	{
		return CTransUtil.newApply("newSetMalloc");
	}

	@Override
	protected List<SStmCG> getConditionalAdd(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		AIdentifierVarExpCG setCompResult = new AIdentifierVarExpCG();
		setCompResult.setType(compType.clone());
		setCompResult.setName(idPattern.getName());
		setCompResult.setIsLambda(false);
		setCompResult.setIsLocal(true);

		SExpCG setAdd = newApply("vdmSetGrow", setCompResult, first.clone());
		return Collections.singletonList(toStm(setAdd));
	}

	@Override
	public List<SStmCG> getPostOuterBlockStms(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns)
	{
		AIdentifierVarExpCG setCompResult = new AIdentifierVarExpCG();
		setCompResult.setType(compType.clone());
		setCompResult.setName(idPattern.getName());
		setCompResult.setIsLambda(false);
		setCompResult.setIsLocal(true);

		SExpCG setAdd = newApply("vdmSetFitt", setCompResult);
		return Collections.singletonList(toStm(setAdd));
	}
}
