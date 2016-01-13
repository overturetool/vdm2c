package org.overture.codegen.cgen.transformations;

import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.cgc.extast.statements.AExpStmCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class FieldIdentifierToFieldGetApplyTrans extends
		DepthFirstAnalysisAdaptor
{
	public TransAssistantCG assist;

	final static String fieldPrefix = "field_tmp_";

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

	static SStmCG exp2stm(SExpCG exp)
	{
		AExpStmCG expstm = new AExpStmCG();
		expstm.setExp(exp);
		return expstm;
	}

	@Override
	public void caseAIdentifierStateDesignatorCG(
			AIdentifierStateDesignatorCG node) throws AnalysisException
	{
		if (node.parent() instanceof AAssignmentStmCG)
		{
			AAssignmentStmCG assignment = (AAssignmentStmCG) node.parent();
			String name = assist.getInfo().getTempVarNameGen().nextVarName(fieldPrefix);

			AIdentifierPatternCG id = new AIdentifierPatternCG();
			id.setName(name);

			AVarDeclCG retVar = new AVarDeclCG();
			retVar.setType(assignment.getExp().getType().clone());
			retVar.setPattern(id);
			retVar.setSourceNode(assignment.getExp().getSourceNode());
			retVar.setExp(assignment.getExp());

			AIdentifierVarExpCG retVarOcc = new AIdentifierVarExpCG();
			retVarOcc.setType(retVar.getType().clone());
			retVarOcc.setName(name);
			retVarOcc.setSourceNode(retVar.getSourceNode());
			retVarOcc.setIsLocal(true);

			// node.setExp(retVarOcc);

			ABlockStmCG replBlock = new ABlockStmCG();
			replBlock.getLocalDefs().add(retVar);

			assist.replaceNodeWith(assignment, replBlock);

			AApplyExpCG apply = new AApplyExpCG();

			AIdentifierVarExpCG getFieldPtrMacroId = new AIdentifierVarExpCG();
			getFieldPtrMacroId.setName("SET_FIELD_PTR");
			getFieldPtrMacroId.setSourceNode(new SourceNode(node.getSourceNode().getVdmNode()));

			apply.setRoot(getFieldPtrMacroId);
			// assist.replaceNodeWith(node, apply);

			String thisClassName = node.getSourceNode().getVdmNode().getAncestor(AClassClassDefinition.class).getName().getName();// the
																																	// containing
			// class
			String fieldClassName = node.getClassName();

			// PDefinition vardef = node.getSourceNode().getVdmNode().getVardef();
			// if (vardef instanceof AInstanceVariableDefinition)
			// {
			// fieldClassName = thisClassName;
			// } else if (vardef instanceof AInheritedDefinition)
			// {
			// AInheritedDefinition idef = (AInheritedDefinition) vardef;
			// fieldClassName = idef.getClassDefinition().getName().getName();
			// }

			// add this type
			apply.getArgs().add(createIdentifier(thisClassName, node.getSourceNode().getVdmNode()));
			
			// add field owner type
			apply.getArgs().add(createIdentifier(fieldClassName, node.getSourceNode().getVdmNode()));
			// add this
						apply.getArgs().add(createIdentifier("this", node.getSourceNode().getVdmNode()));

			// add field name
			apply.getArgs().add(createIdentifier(node.getName(), node.getSourceNode().getVdmNode()));

			// add new value
			apply.getArgs().add(retVarOcc.clone());

			AExpStmCG expstm = new AExpStmCG();
			expstm.setExp(apply);

			replBlock.getStatements().add(expstm);

			AApplyExpCG vdmFree = new AApplyExpCG();
			vdmFree.setRoot(createIdentifier("vdmFree", node.getSourceNode().getVdmNode()));
			vdmFree.getArgs().add(retVarOcc);

			replBlock.getStatements().add(exp2stm(vdmFree));
		}
	}

}