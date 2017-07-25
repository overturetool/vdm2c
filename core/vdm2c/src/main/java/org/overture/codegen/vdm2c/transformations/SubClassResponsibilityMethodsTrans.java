package org.overture.codegen.vdm2c.transformations;

import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.ASubclassResponsibilityExp;
import org.overture.ast.node.INode;
import org.overture.ast.statements.ASubclassResponsibilityStm;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AVoidType;
import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.expressions.ANullExpIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.utils.CGenUtil;

public class SubClassResponsibilityMethodsTrans extends
DepthFirstAnalysisCAdaptor
{

	public SubClassResponsibilityMethodsTrans(
			TransAssistantIR transformationAssistant)
	{
	}

	@Override
	public void caseAMethodDeclIR(AMethodDeclIR node) throws AnalysisException
	{
		//TODO:  Investigate whether this can be done more cleanly without looking at the source VDM node.
		if (node.getSourceNode() == null
				|| node.getSourceNode().getVdmNode() == null)
		{
			return;
		}
		INode vdm = node.getSourceNode().getVdmNode();

		if (vdm instanceof SOperationDefinition
				&& ((SOperationDefinition) vdm).getBody() instanceof ASubclassResponsibilityStm
				|| vdm instanceof SFunctionDefinition
				&& ((SFunctionDefinition) vdm).getBody() instanceof ASubclassResponsibilityExp)
		{
			ABlockStmIR stm = new ABlockStmIR();

			if(vdm instanceof SOperationDefinition)
			{	
				if(((AOperationType) ((SOperationDefinition) vdm).getType()).getResult() instanceof AVoidType)
				{				
					stm.getStatements().add(new AReturnStmIR());
				}
				else
				{
					AReturnStmIR retstm = new AReturnStmIR();

					retstm.setExp(CGenUtil.consCNull());
					stm.getStatements().add(retstm);
				}
			}
			else if(vdm instanceof SFunctionDefinition)
			{
				if(((AFunctionType) ((SFunctionDefinition) vdm).getType()).getResult() instanceof AVoidType)
				{				
					stm.getStatements().add(new AReturnStmIR());
				}
				else
				{
					AReturnStmIR retstm = new AReturnStmIR();
					retstm.setExp(CGenUtil.consCNull());
					stm.getStatements().add(retstm);
				}
			}
			node.setBody(stm);
		}

	}

}
