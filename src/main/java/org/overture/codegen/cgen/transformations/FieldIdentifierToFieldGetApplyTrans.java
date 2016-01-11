package org.overture.codegen.cgen.transformations;

import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class FieldIdentifierToFieldGetApplyTrans extends
		DepthFirstAnalysisAdaptor
{
	public TransAssistantCG assist;

	final static String retPrefix = "ret_";

	public FieldIdentifierToFieldGetApplyTrans(TransAssistantCG assist)
	{
		this.assist = assist;
	}

	AIdentifierVarExpCG createIdentifier(String name)
	{
		AIdentifierVarExpCG ident = new AIdentifierVarExpCG();
		ident.setName(name);
		return ident;
	}

	@Override
	public void caseAIdentifierVarExpCG(AIdentifierVarExpCG node)
			throws AnalysisException
	{
		if (node.getIsLocal())
			return;

		if (node.getSourceNode().getVdmNode() instanceof AVariableExp
				&& ((AVariableExp) node.getSourceNode().getVdmNode()).getVardef() instanceof AInstanceVariableDefinition)
		{
			AApplyExpCG apply = new AApplyExpCG();

			AIdentifierVarExpCG getFieldPtrMacroId = new AIdentifierVarExpCG();
			getFieldPtrMacroId.setName("GET_FIELD_PTR");

			apply.setRoot(getFieldPtrMacroId);
			assist.replaceNodeWith(node, apply);

			AVariableExp var = ((AVariableExp) node.getSourceNode().getVdmNode());
			String thisClassName = var.getVardef().getClassDefinition().getName().getName();
			String fieldClassName = thisClassName;

			// add this type
			apply.getArgs().add(createIdentifier(thisClassName));
			// add field owner type
			apply.getArgs().add(createIdentifier(fieldClassName));
			// add this
			apply.getArgs().add(createIdentifier("this"));
			// add field name
			apply.getArgs().add(node);
		}
	}

}