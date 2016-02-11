package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.createIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.exp2Stm;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newDeclarationAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newMacroApply;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.name.ATokenNameIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;

public class FieldIdentifierToFieldGetApplyTrans extends
		DepthFirstAnalysisCAdaptor
{
	public TransAssistantIR assist;

	final static String fieldPrefix = "field_tmp_";

	public FieldIdentifierToFieldGetApplyTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseAIdentifierVarExpIR(AIdentifierVarExpIR node)
			throws AnalysisException
	{
		if (node.getIsLocal())
		{
			return;
		}

		String thisClassName = null;
		String fieldClassName = null;

		INode vdmNode = node.getSourceNode().getVdmNode();
		if (vdmNode instanceof AVariableExp)
		{
			AVariableExp varExp = (AVariableExp) vdmNode;

			if (varExp.getType() instanceof AFunctionType
					|| varExp.getType() instanceof AOperationType)
			{
				return;
			}

			thisClassName = varExp.getAncestor(AClassClassDefinition.class).getName().getName();// the containing
																								// class

			PDefinition vardef = varExp.getVardef();
			if (vardef instanceof AInstanceVariableDefinition)
			{
				fieldClassName = thisClassName;
			} else if (vardef instanceof AInheritedDefinition)
			{
				AInheritedDefinition idef = (AInheritedDefinition) vardef;
				fieldClassName = idef.getClassDefinition().getName().getName();
			} else if (vardef instanceof ALocalDefinition
					&& ((ALocalDefinition) vardef).getValueDefinition())
			{
				return;
			}

		} else if (vdmNode instanceof AIdentifierStateDesignator)
		{
			AIdentifierStateDesignator designator = (AIdentifierStateDesignator) vdmNode;

			// why is the definition not kept here? we dont know what this points to

			AClassClassDefinition thisClass = designator.getAncestor(AClassClassDefinition.class);// the containing
																									// class
			thisClassName = thisClass.getName().getName();

//			while (thisClass != null && fieldClassName == null)
			{
				List<PDefinition> definitons = new Vector<PDefinition>();
				definitons.addAll(thisClass.getDefinitions());
				definitons.addAll(thisClass.getAllInheritedDefinitions());
				for (PDefinition def : definitons)
				{
					if (def instanceof AInheritedDefinition)
					{
						def = ((AInheritedDefinition)def).getSuperdef();
					}
					
					if (def instanceof AInstanceVariableDefinition)
					{
						if (def.getName().getName().equals(designator.getName().getName()))
						{
							fieldClassName = def.getClassDefinition().getName().getName();
							break;
						}
					} 
				}
			}

			if (fieldClassName == null)
			{
				System.out.println();
			}
		}

		if (thisClassName != null && fieldClassName != null)
		{

			AMacroApplyExpIR apply = newMacroApply("GET_FIELD_PTR");
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

	String lookupFieldClass(SClassDeclIR node, String name)
	{
		for (AFieldDeclIR f : node.getFields())
		{
			if (f.getName().equals(name))
			{
				return node.getName();
			}
		}

		for (ATokenNameIR superName : node.getSuperNames())
		{
			for (SClassDeclIR def : assist.getInfo().getClasses())
			{
				if (def.getName().equals(superName))
				{
					String n = lookupFieldClass(def, name);
					if (n != null)
					{
						return n;
					}
				}
			}
		}

		return null;
	}

	@Override
	public void caseAAssignToExpStmIR(AAssignToExpStmIR node)
			throws AnalysisException
	{
		if (node.getTarget() instanceof AIdentifierVarExpIR
				&& ((AIdentifierVarExpIR) node.getTarget()).getIsLocal())
		{
			return;
		}

		AIdentifierVarExpIR target = (AIdentifierVarExpIR) node.getTarget();
		// class
		String thisClassName = target.getSourceNode().getVdmNode().getAncestor(AClassClassDefinition.class).getName().getName();// the
																																// containing
		// field owner
		String fieldClassName = lookupFieldClass(target.getAncestor(ADefaultClassDeclIR.class), target.getName());

		String name = assist.getInfo().getTempVarNameGen().nextVarName(fieldPrefix);

		AVarDeclIR retVar = newDeclarationAssignment(name, node.getExp().getType().clone(), node.getExp(), node.getExp().getSourceNode());

		ABlockStmIR replBlock = new ABlockStmIR();
		replBlock.setScoped(true);
		replBlock.getLocalDefs().add(retVar);

		assist.replaceNodeWith(node, replBlock);

		AMacroApplyExpIR apply = newMacroApply("SET_FIELD_PTR");// new AApplyExpIR();
		apply.setSourceNode(target.getSourceNode());

		// add this type
		apply.getArgs().add(createIdentifier(thisClassName, target.getSourceNode()));

		// add field owner type
		apply.getArgs().add(createIdentifier(fieldClassName, target.getSourceNode()));
		// add this
		apply.getArgs().add(createIdentifier("this", target.getSourceNode()));

		// add field name
		apply.getArgs().add(createIdentifier(target.getName(), target.getSourceNode()));

		// add new value
		retVar.getSourceNode();
		apply.getArgs().add(createIdentifier(name, retVar.getSourceNode()));

		replBlock.getStatements().add(exp2Stm(apply));

		AApplyExpIR vdmFree = new AApplyExpIR();
		vdmFree.setRoot(createIdentifier("vdmFree", node.getSourceNode()));
		vdmFree.getArgs().add(createIdentifier(name, retVar.getSourceNode()));

		replBlock.getStatements().add(exp2Stm(vdmFree));
	}

}