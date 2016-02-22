package org.overture.codegen.vdm2c.transformations;

import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.ASubclassResponsibilityExp;
import org.overture.ast.node.INode;
import org.overture.ast.statements.ASubclassResponsibilityStm;
import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

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
			node.setBody(new ABlockStmIR());
		}

	}

}
