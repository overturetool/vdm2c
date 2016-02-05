package org.overture.codegen.vdm2c.utils;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newIntLiteralExp;
import static org.overture.codegen.vdm2c.utils.CTransUtil.toStm;

import java.util.Collections;
import java.util.List;

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.comp.SetCompStrategy;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public class CSetCompStrategy extends SetCompStrategy
{

	public CSetCompStrategy(TransAssistantIR transformationAssitant,
			SExpIR first, SExpIR predicate, String var, STypeIR compType,
			ILanguageIterator langIterator, ITempVarGen tempGen,
			IterationVarPrefixes iteVarPrefixes)
	{
		super(transformationAssitant, first, predicate, var, compType, langIterator, tempGen, iteVarPrefixes);
	}

	@Override
	protected SExpIR getEmptyCollection()
	{
		return CTransUtil.newApply("newSetVarToGrow", newIntLiteralExp(0), newIntLiteralExp(5));// TODO: request
																								// Framework change such
																								// that this is inserted
																								// after the set is
																								// Evaluated such that a
																								// useful estimate can
																								// be made of the size
	}

	@Override
	protected List<SStmIR> getConditionalAdd(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
	{
		AIdentifierVarExpIR setCompResult = new AIdentifierVarExpIR();
		setCompResult.setType(compType.clone());
		setCompResult.setName(idPattern.getName());
		setCompResult.setIsLambda(false);
		setCompResult.setIsLocal(true);

		SExpIR setAdd = newApply("vdmSetGrow", setCompResult, first.clone());
		return Collections.singletonList(toStm(setAdd));
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

		SExpIR setAdd = newApply("vdmSetFit", setCompResult);
		return Collections.singletonList(toStm(setAdd));
	}
}
