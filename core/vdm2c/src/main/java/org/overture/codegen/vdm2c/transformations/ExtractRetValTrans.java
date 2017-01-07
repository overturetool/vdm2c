package org.overture.codegen.vdm2c.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

//Extract return value to local variable and return that.
public class ExtractRetValTrans extends DepthFirstAnalysisCAdaptor
{
	public TransAssistantIR assist;

	final static String retPrefix = "ret_";

	public ExtractRetValTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseAReturnStmIR(AReturnStmIR node) throws AnalysisException
	{
		if (node.getExp() == null || node.getExp().getType() == null)
		{
			return;
		}
		// ret = clone(a)
		// free block
		// return ret

		String name = assist.getInfo().getTempVarNameGen().nextVarName(retPrefix);

		AIdentifierPatternIR id = new AIdentifierPatternIR();
		id.setName(name);

		AVarDeclIR retVar = new AVarDeclIR();
		retVar.setType(node.getExp().getType().clone());
		retVar.setPattern(id);
		retVar.setSourceNode(node.getExp().getSourceNode());
		
		retVar.setExp(ValueSemantics.clone(node.getExp()));

		AIdentifierVarExpIR retVarOcc = new AIdentifierVarExpIR();
		retVarOcc.setType(retVar.getType().clone());
		retVarOcc.setName(name);
		retVarOcc.setSourceNode(retVar.getSourceNode());
		retVarOcc.setIsLocal(true);

		node.setExp(retVarOcc);

		ABlockStmIR replBlock = new ABlockStmIR();
		replBlock.getLocalDefs().add(retVar);

		assist.replaceNodeWith(node, replBlock);

		replBlock.getStatements().add(node);
	}
}