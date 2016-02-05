package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.toExp;
import static org.overture.codegen.vdm2c.utils.CTransUtil.*;

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.ALetDefExpIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class LetTrans extends DepthFirstAnalysisAdaptor
{
	public TransAssistantIR assist;

	public LetTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}


	@Override
	public void caseALetDefExpIR(ALetDefExpIR node) throws AnalysisException
	{
		ABlockStmIR replBlock = new ABlockStmIR();
		replBlock.setScoped(true);
		
		for (AVarDeclIR varDef : node.getLocalDefs())
		{
			varDef.apply(THIS);
		}
		
		replBlock.setLocalDefs(node.getLocalDefs());
		
		node.getExp().apply(THIS);
		replBlock.getStatements().add(toStm(node.getExp()));
		
		SExpIR replacement =newParen( toExp(replBlock));
		assist.replaceNodeWith(node, replacement);
	}
	
	
	
	
	
}