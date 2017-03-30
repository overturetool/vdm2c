package org.overture.codegen.vdm2c;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newDeclarationAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newIntLiteralExp;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newTvpType;
import static org.overture.codegen.vdm2c.utils.CTransUtil.toExp;

import java.util.List;

import org.overture.codegen.assistant.AssistantBase;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.ALocalPatternAssignmentStmIR;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.iterator.AbstractLanguageIterator;
import org.overture.codegen.vdm2c.utils.CTransUtil;

public class CForIterator extends AbstractLanguageIterator
{

	public CForIterator(TransAssistantIR transformationAssistant,
			IterationVarPrefixes iteVarPrefixes)
	{
		super(transformationAssistant, iteVarPrefixes);
	}

	protected String iteratorName;

	String setName = null;

	@Override
	public List<SStmIR> getPreForLoopStms(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
	{
		// Generate nothing
		return null;
	}

	@Override
	public AVarDeclIR getForLoopInit(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
	{
		iteratorName = transAssistant.getInfo().getTempVarNameGen().nextVarName(iteVarPrefixes.iterator());
		setName = setVar.getName();
		SExpIR getIteratorCall = CTransUtil.newIntLiteralExp(0);

		return transAssistant.getInfo().getDeclAssistant().consLocalVarDecl(newTvpType(), transAssistant.getInfo().getPatternAssistant().consIdPattern(iteratorName), getIteratorCall);
	}

	@Override
	public SExpIR getForLoopCond(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
			throws AnalysisException
	{
		return newApply("vdmLessThan", newIdentifier(iteratorName, null), newApply("vdmSetCard", newIdentifier(setName, null)));
	}

	@Override
	public SExpIR getForLoopInc(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
	{
		return toExp(newAssignment(newIdentifier(iteratorName, null), newApply("vdmSum", newIdentifier(iteratorName, null), newIntLiteralExp(1))));
	}

	@Override
	public AVarDeclIR getNextElementDeclared(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
			throws AnalysisException
	{
		AIdentifierVarExpIR setId = newIdentifier(setName, null);
		setId.setIsLocal(true);
		AIdentifierVarExpIR itrId = newIdentifier(iteratorName, null);
		itrId.setIsLocal(true);
		return newDeclarationAssignment(pattern, newTvpType(), newApply("vdmSetElementAt", setId, newApply("toInteger", itrId)), null);
	}

	@Override
	public ALocalPatternAssignmentStmIR getNextElementAssigned(
			AIdentifierVarExpIR setVar, List<SPatternIR> patterns,
			SPatternIR pattern, AVarDeclIR successVarDecl,
			AVarDeclIR nextElementDecl) throws AnalysisException
	{
		if(!(pattern instanceof AIdentifierPatternIR))
		{
			transAssistant.getInfo().addUnsupportedNode(AssistantBase.getVdmNode(pattern), "This transformation currently only support identifier patterns");
			return null;
		}
		
		ALocalPatternAssignmentStmIR assign = new ALocalPatternAssignmentStmIR();
		assign.setNextElementDecl(nextElementDecl);
		assign.setTarget(pattern);
		assign.setExp(newApply("vdmSetElementAt", newIdentifier(setName, null), newApply("toInteger", newIdentifier(iteratorName, null))));
		
		return assign;
	}

	@Override
	public SExpIR consNextElementCall(AIdentifierVarExpIR setVar)
			throws AnalysisException
	{
		return null;
	}
	
	public String getIteratorName() {
		return iteratorName;
	}

}
