package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.toStm;

import java.util.LinkedList;

import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.statements.ALocalVariableDeclarationStmIR;

public class FreeLocalBlockDeclsTrans extends DepthFirstAnalysisAdaptor
{
	public TransAssistantIR assist;

	public FreeLocalBlockDeclsTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseABlockStmIR(ABlockStmIR node) throws AnalysisException
	{
		LinkedList<SStmIR> stms;
		AApplyExpIR applyexpr;
		AIdentifierVarExpIR toomit;
		int addoffset;

		super.caseABlockStmIR(node);

		stms = (LinkedList<SStmIR>) ((ABlockStmIR) node).getStatements().clone();

		addoffset = 0;
		
		//If this statement block ends in a return statement.
		if(stms.getLast() instanceof AReturnStmIR)
		{
			//Where to insert the vdmFree statements.
			addoffset = 1;

			//Remove the return variable declaration from the list of those to have corresponding vdmFree statements.
			if(((AReturnStmIR) stms.getLast()).getExp() instanceof AIdentifierVarExpIR)
			{
				toomit = ((AIdentifierVarExpIR)((AReturnStmIR) stms.getLast()).getExp());

				for(SStmIR stm : stms)
				{
					if(stm instanceof ALocalVariableDeclarationStmIR)
					{
						if(((AIdentifierPatternIR) ((ALocalVariableDeclarationStmIR) stm).getDecleration().getPattern()).getName().equals(toomit.getName()))
						{
							stms.remove(stm);
							break;
						}
					}
				}
			}
		}

		for (SStmIR stm : stms)
		{
			if (stm instanceof ALocalVariableDeclarationStmIR)
			{
				if (((ALocalVariableDeclarationStmIR) stm).getDecleration().getPattern() instanceof AIdentifierPatternIR)
				{
					applyexpr = newApply("vdmFree", newIdentifier(((AIdentifierPatternIR) ((ALocalVariableDeclarationStmIR) stm).getDecleration().getPattern()).getName(), null));
					((ABlockStmIR) node).getStatements().add(node.getStatements().size() - addoffset, toStm(applyexpr));					
				}
			}
		}
	}
}