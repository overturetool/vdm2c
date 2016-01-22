package org.overture.codegen.vdm2c.transformations;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class ExtractRetValTrans extends DepthFirstAnalysisAdaptor
{
	public TransAssistantCG assist;

	final static String retPrefix = "ret_";

	public ExtractRetValTrans(TransAssistantCG assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseAReturnStmCG(AReturnStmCG node) throws AnalysisException
	{
		if(node.getExp()==null||node.getExp().getType()==null)
		{
			return;
		}
		// ret = clone(a)
		// free block
		// return ret

		String name = assist.getInfo().getTempVarNameGen().nextVarName(retPrefix);

		AIdentifierPatternCG id = new AIdentifierPatternCG();
		id.setName(name);

		AVarDeclCG retVar = new AVarDeclCG();
		retVar.setType(node.getExp().getType().clone());
		retVar.setPattern(id);
		retVar.setSourceNode(node.getExp().getSourceNode());
		retVar.setExp(node.getExp());

		AIdentifierVarExpCG retVarOcc = new AIdentifierVarExpCG();
		retVarOcc.setType(retVar.getType().clone());
		retVarOcc.setName(name);
		retVarOcc.setSourceNode(retVar.getSourceNode());
		retVarOcc.setIsLocal(true);

		node.setExp(retVarOcc);

		ABlockStmCG replBlock = new ABlockStmCG();
		replBlock.getLocalDefs().add(retVar);

		assist.replaceNodeWith(node, replBlock);

		replBlock.getStatements().add(node);
	}
}