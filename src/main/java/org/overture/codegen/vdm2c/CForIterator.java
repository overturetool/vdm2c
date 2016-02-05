package org.overture.codegen.vdm2c;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newDeclarationAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newIntLiteralExp;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newTvpType;
import static org.overture.codegen.vdm2c.utils.CTransUtil.toExp;

import java.util.List;

import org.overture.codegen.ir.SExpCG;
import org.overture.codegen.ir.SPatternCG;
import org.overture.codegen.ir.SStmCG;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AVarDeclCG;
import org.overture.codegen.ir.expressions.AIdentifierVarExpCG;
import org.overture.codegen.ir.statements.ALocalPatternAssignmentStmCG;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.iterator.AbstractLanguageIterator;
import org.overture.codegen.vdm2c.utils.CTransUtil;

public class CForIterator extends AbstractLanguageIterator
{

	public CForIterator(TransAssistantCG transformationAssistant,
			IterationVarPrefixes iteVarPrefixes)
	{
		super(transformationAssistant, iteVarPrefixes);
	}

	protected String iteratorName;

	String setName = null;

	@Override
	public List<SStmCG> getPreForLoopStms(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		// Generate nothing
		return null;
	}

	@Override
	public AVarDeclCG getForLoopInit(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		iteratorName = transAssistant.getInfo().getTempVarNameGen().nextVarName(iteVarPrefixes.iterator());
		setName = setVar.getName();
		SExpCG getIteratorCall = CTransUtil.newIntLiteralExp(0);

		return transAssistant.getInfo().getDeclAssistant().consLocalVarDecl(newTvpType(), transAssistant.getInfo().getPatternAssistant().consIdPattern(iteratorName), getIteratorCall);
	}

	@Override
	public SExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException
	{
		return newApply("vdmLessThan", newIdentifier(iteratorName, null), newApply("vdmSetCard", newIdentifier(setName, null)));
	}

	@Override
	public SExpCG getForLoopInc(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		return toExp(newAssignment(newIdentifier(iteratorName, null), newApply("vdmSum", newIdentifier(iteratorName, null), newIntLiteralExp(1))));
	}

	@Override
	public AVarDeclCG getNextElementDeclared(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException
	{
		AIdentifierVarExpCG setId = newIdentifier(setName, null);
		setId.setIsLocal(true);
		AIdentifierVarExpCG itrId = newIdentifier(iteratorName, null);
		itrId.setIsLocal(true);
		return newDeclarationAssignment(pattern, newTvpType(), newApply("vdmSetElementAt", setId, itrId), null);
	}

	@Override
	public ALocalPatternAssignmentStmCG getNextElementAssigned(
			AIdentifierVarExpCG setVar, List<SPatternCG> patterns,
			SPatternCG pattern, AVarDeclCG successVarDecl,
			AVarDeclCG nextElementDecl) throws AnalysisException
	{
		return null;
	}

	@Override
	public SExpCG consNextElementCall(AIdentifierVarExpCG setVar)
			throws AnalysisException
	{
		return null;
	}
	
	

}
