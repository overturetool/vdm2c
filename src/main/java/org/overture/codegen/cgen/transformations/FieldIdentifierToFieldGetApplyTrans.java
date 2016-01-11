package org.overture.codegen.cgen.transformations;

import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.ir.SourceNode;
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

	AIdentifierVarExpCG createIdentifier(String name,
			org.overture.ast.node.INode derrivedFrom)
	{
		AIdentifierVarExpCG ident = new AIdentifierVarExpCG();
		ident.setName(name);
		ident.setSourceNode(new SourceNode(derrivedFrom));
		return ident;
	}

	@Override
	public void caseAIdentifierVarExpCG(AIdentifierVarExpCG node)
			throws AnalysisException
	{
		if (node.getIsLocal())
			return;

		if (node.getSourceNode().getVdmNode() instanceof AVariableExp)
		{
			AVariableExp varExp = (AVariableExp) node.getSourceNode().getVdmNode();

			String thisClassName = varExp.getAncestor(AClassClassDefinition.class).getName().getName();// the containing
																										// class
			String fieldClassName = null;

			PDefinition vardef = varExp.getVardef();
			if (vardef instanceof AInstanceVariableDefinition)
			{
				fieldClassName = thisClassName;
			} else if (vardef instanceof AInheritedDefinition)
			{
				AInheritedDefinition idef = (AInheritedDefinition) vardef;
				fieldClassName = idef.getClassDefinition().getName().getName();
			}

			AApplyExpCG apply = new AApplyExpCG();

			AIdentifierVarExpCG getFieldPtrMacroId = new AIdentifierVarExpCG();
			getFieldPtrMacroId.setName("GET_FIELD_PTR");
			getFieldPtrMacroId.setSourceNode(new SourceNode(node.getSourceNode().getVdmNode()));

			apply.setRoot(getFieldPtrMacroId);
			assist.replaceNodeWith(node, apply);

			// AVariableExp var = vdmNode;
			// String thisClassName = var.getVardef().getClassDefinition().getName().getName();
			// String fieldClassName = thisClassName;

			// add this type
			apply.getArgs().add(createIdentifier(thisClassName, node.getSourceNode().getVdmNode()));
			// add field owner type
			apply.getArgs().add(createIdentifier(fieldClassName, node.getSourceNode().getVdmNode()));
			// add this
			apply.getArgs().add(createIdentifier("this", node.getSourceNode().getVdmNode()));
			// add field name
			apply.getArgs().add(node);
		}
	}
}