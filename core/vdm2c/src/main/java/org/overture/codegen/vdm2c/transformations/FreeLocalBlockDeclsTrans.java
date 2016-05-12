package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.rewriteToApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.toStm;

import java.util.LinkedList;

import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.analysis.intf.IAnalysis;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.statements.ALocalVariableDeclarationStmIR;
import org.overture.codegen.vdm2c.utils.IApplyAssistant;

public class FreeLocalBlockDeclsTrans extends DepthFirstAnalysisAdaptor implements IApplyAssistant
{
	public TransAssistantIR assist;

	@Override
	public TransAssistantIR getAssist()
	{
		return assist;
	}

	@Override
	public IAnalysis getAnalyzer()
	{
		return THIS;
	}

	public FreeLocalBlockDeclsTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseAAssignToExpStmIR(AAssignToExpStmIR node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseAAssignToExpStmIR(node);
	}

	@Override
	public void caseABlockStmIR(ABlockStmIR node) throws AnalysisException
	{
		LinkedList<SStmIR> stms;
		AApplyExpIR applyexpr;
		AIdentifierVarExpIR toomit;
		int addoffset;

		super.caseABlockStmIR(node);

		//Cloned in order to take a snapshot, since the node's list will change
		//as vdmFree function application nodes are inserted.
		stms = (LinkedList<SStmIR>) ((ABlockStmIR) node).getStatements().clone();

		//Where to insert the vdmFree statements if this block does
		//not end in a return statement.
		addoffset = 0;

		//Phase to insert vdmFree statements for local definitions.
		//If this statement block ends in a return statement, ignore the return variable.
		if(stms.getLast() instanceof AReturnStmIR)
		{
			//Where to insert the vdmFree statements when the block ends in
			//a return statement.
			addoffset = 1;

			//Remove the return variable declaration from the list of those to have corresponding vdmFree statements.
			if(((AReturnStmIR) stms.getLast()).getExp() instanceof AIdentifierVarExpIR)
			{
				toomit = ((AIdentifierVarExpIR)((AReturnStmIR) stms.getLast()).getExp());

				for(SStmIR stm : stms)
				{
					if(stm instanceof ALocalVariableDeclarationStmIR)
					{
						if(((ALocalVariableDeclarationStmIR) stm).getDecleration().getPattern() instanceof AIdentifierPatternIR)
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
		}	

		//Determine which declarations need vdmFree statements.
		for (SStmIR stm : stms)
		{
			if (stm instanceof ALocalVariableDeclarationStmIR)
			{						
				AVarDeclIR decleration = ((ALocalVariableDeclarationStmIR) stm).getDecleration();
				if ((decleration.getPattern() instanceof AIdentifierPatternIR) &&
						!(decleration.getType() instanceof AExternalTypeIR))
				{
					applyexpr = newApply("vdmFree", newIdentifier(((AIdentifierPatternIR) decleration.getPattern()).getName(), null));
					((ABlockStmIR) node).getStatements().add(node.getStatements().size() - addoffset, toStm(applyexpr));
				}
			}
		}

		//Phase to change assignments to vdmClone.  This is the brutal
		//way to deal with the problem of pointer aliasing.
		for(SStmIR stm : ((ABlockStmIR) node).getStatements())
		{
			if (stm instanceof ALocalVariableDeclarationStmIR)
			{
				if(!(((ALocalVariableDeclarationStmIR) stm).getDecleration().getType() instanceof AExternalTypeIR))
				{
					rewriteToApply(this,
							((ALocalVariableDeclarationStmIR) stm).getDecleration().getExp(),
							"vdmClone",
							((ALocalVariableDeclarationStmIR) stm).getDecleration().getExp());
				}	
			}
		}
	}
}