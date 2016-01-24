package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.createIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.exp2Stm;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newDeclarationAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newMacroApply;

import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.declarations.SClassDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.name.ATokenNameCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpCG;

public class FieldIdentifierToFieldGetApplyTrans extends
		DepthFirstAnalysisAdaptor
{
	public TransAssistantCG assist;

	final static String fieldPrefix = "field_tmp_";

	public FieldIdentifierToFieldGetApplyTrans(TransAssistantCG assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseAIdentifierVarExpCG(AIdentifierVarExpCG node)
			throws AnalysisException
	{
		if (node.getIsLocal())
			return;

		INode vdmNode = node.getSourceNode().getVdmNode();
		if (vdmNode instanceof AVariableExp )
		{
			AVariableExp varExp = (AVariableExp) vdmNode;

			if(varExp.getType() instanceof AFunctionType || varExp.getType() instanceof AOperationType)
				return;
			
			
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

			AMacroApplyExpCG apply = newMacroApply("GET_FIELD_PTR");
			assist.replaceNodeWith(node, apply);

			// add this type
			apply.getArgs().add(createIdentifier(thisClassName, node.getSourceNode()));
			// add field owner type
			apply.getArgs().add(createIdentifier(fieldClassName, node.getSourceNode()));
			// add this
			apply.getArgs().add(createIdentifier("this", node.getSourceNode()));
			// add field name
			apply.getArgs().add(node);
		}
	}

	String lookupFieldClass(SClassDeclCG node, String name)
	{
		for (AFieldDeclCG f : node.getFields())
		{
			if (f.getName().equals(name))
			{
				return node.getName();
			}
		}

		for (ATokenNameCG superName : node.getSuperNames())
		{
			for (SClassDeclCG def : assist.getInfo().getClasses())
			{
				if (def.getName().equals(superName))
				{
					String n = lookupFieldClass(def, name);
					if (n != null)
						return n;
				}
			}
		}

		return null;
	}

	@Override
	public void caseAIdentifierStateDesignatorCG(
			AIdentifierStateDesignatorCG node) throws AnalysisException
	{
		if (node.getIsLocal())
			return;
		if (node.parent() instanceof AAssignmentStmCG)
		{
			// class
			String thisClassName = node.getSourceNode().getVdmNode().getAncestor(AClassClassDefinition.class).getName().getName();// the
																																	// containing
			// field owner
			String fieldClassName = lookupFieldClass(node.getAncestor(ADefaultClassDeclCG.class), node.getName());

			AAssignmentStmCG assignment = (AAssignmentStmCG) node.parent();
			String name = assist.getInfo().getTempVarNameGen().nextVarName(fieldPrefix);

			AVarDeclCG retVar = newDeclarationAssignment(name, assignment.getExp().getType().clone(), assignment.getExp(), assignment.getExp().getSourceNode());

			ABlockStmCG replBlock = new ABlockStmCG();
			replBlock.setScoped(true);
			replBlock.getLocalDefs().add(retVar);

			assist.replaceNodeWith(assignment, replBlock);

			AMacroApplyExpCG apply = newMacroApply("SET_FIELD_PTR");// new AApplyExpCG();
			apply.setSourceNode(node.getSourceNode());

			// add this type
			apply.getArgs().add(createIdentifier(thisClassName, node.getSourceNode()));

			// add field owner type
			apply.getArgs().add(createIdentifier(fieldClassName, node.getSourceNode()));
			// add this
			apply.getArgs().add(createIdentifier("this", node.getSourceNode()));

			// add field name
			apply.getArgs().add(createIdentifier(node.getName(), node.getSourceNode()));

			// add new value
			retVar.getSourceNode();
			apply.getArgs().add(createIdentifier(name, retVar.getSourceNode()));

			replBlock.getStatements().add(exp2Stm(apply));

			AApplyExpCG vdmFree = new AApplyExpCG();
			vdmFree.setRoot(createIdentifier("vdmFree", node.getSourceNode()));
			vdmFree.getArgs().add(createIdentifier(name, retVar.getSourceNode()));

			replBlock.getStatements().add(exp2Stm(vdmFree));
		}
	}

}