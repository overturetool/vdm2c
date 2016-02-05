package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.toExp;
import static org.overture.codegen.vdm2c.utils.CTransUtil.*;

import org.overture.codegen.ir.SExpCG;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AVarDeclCG;
import org.overture.codegen.ir.expressions.ALetDefExpCG;
import org.overture.codegen.ir.statements.ABlockStmCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class LetTrans extends DepthFirstAnalysisAdaptor
{
	public TransAssistantCG assist;

	public LetTrans(TransAssistantCG assist)
	{
		this.assist = assist;
	}


	@Override
	public void caseALetDefExpCG(ALetDefExpCG node) throws AnalysisException
	{
		ABlockStmCG replBlock = new ABlockStmCG();
		replBlock.setScoped(true);
		
		for (AVarDeclCG varDef : node.getLocalDefs())
		{
			varDef.apply(THIS);
		}
		
		replBlock.setLocalDefs(node.getLocalDefs());
		
		node.getExp().apply(THIS);
		replBlock.getStatements().add(toStm(node.getExp()));
		
		SExpCG replacement =newParen( toExp(replBlock));
		assist.replaceNodeWith(node, replacement);
	}
	
	
	
	
	
}