package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.toStm;

import java.util.LinkedList;

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.AElseIfStmIR;
import org.overture.codegen.ir.statements.AIfStmIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.statements.ALocalVariableDeclarationStmIR;

public class IfTrans extends DepthFirstAnalysisAdaptor
{
	public TransAssistantIR assist;

	public IfTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	boolean mustUnpack(STypeIR type)
	{
		return type != null && !(type instanceof AExternalTypeIR);
	}

	void unpack(SExpIR exp)
	{
		assist.replaceNodeWith(exp, newApply("toBool", exp.clone()));
	}

	void process(SExpIR exp)
	{
		if (mustUnpack(exp.getType()))
		{
			unpack(exp);
		}
	}

	@Override
	public void caseAIfStmIR(AIfStmIR node) throws AnalysisException
	{
		super.caseAIfStmIR(node);
		process(node.getIfExp());

		for (AElseIfStmIR elseIf : node.getElseIf())
		{
			process(elseIf.getElseIf());
		}
		
		LinkedList<SStmIR> stms;
		AApplyExpIR applyexpr;

//		if(node.getThenStm() instanceof ABlockStmIR)
//		{
//			stms = (LinkedList<SStmIR>) ((ABlockStmIR) (node.getThenStm())).getStatements().clone();			
//
//			for (SStmIR stm : stms)
//			{
//				if (stm instanceof ALocalVariableDeclarationStmIR)
//				{
//					if(((ALocalVariableDeclarationStmIR) stm).getDecleration().getPattern() instanceof AIdentifierPatternIR)
//					{
//						applyexpr = newApply("vdmFree", newIdentifier(
//								((AIdentifierPatternIR) ((ALocalVariableDeclarationStmIR) stm)
//										.getDecleration()
//										.getPattern())
//								.getName(), null));
//
//						((ABlockStmIR) (node.getThenStm())).getStatements().add(toStm(applyexpr));
//					}
//				}
//			}
//		}
	}
}