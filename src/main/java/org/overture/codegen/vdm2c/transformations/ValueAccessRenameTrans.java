package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newIdentifier;

import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.expressions.AExplicitVarExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.vdm2c.utils.NameConverter;

public class ValueAccessRenameTrans extends DepthFirstAnalysisAdaptor
{
	public TransAssistantCG assist;

	public ValueAccessRenameTrans(TransAssistantCG assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseAExplicitVarExpCG(AExplicitVarExpCG node)
			throws AnalysisException
	{
		INode vdmNode = node.getSourceNode().getVdmNode();
		if (vdmNode instanceof AVariableExp)
		{
			AVariableExp varExp = (AVariableExp) vdmNode;
			if (varExp.getVardef() instanceof ALocalDefinition)
			{
				ALocalDefinition local = (ALocalDefinition) varExp.getVardef();
				if (local.getValueDefinition())
				{
					AIdentifierVarExpCG identifier = newIdentifier(NameConverter.getCFieldNameFromOriginal(node.getName()), node.getSourceNode());
					identifier.setIsLocal(false);
					assist.replaceNodeWith(node, identifier);
				}
			}

		}
		super.caseAExplicitVarExpCG(node);
	}

}
