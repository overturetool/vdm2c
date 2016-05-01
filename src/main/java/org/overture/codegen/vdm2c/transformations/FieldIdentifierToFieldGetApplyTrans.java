package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.GET_FIELD_PTR;
import static org.overture.codegen.vdm2c.utils.CTransUtil.SET_FIELD_PTR;
import static org.overture.codegen.vdm2c.utils.CTransUtil.createIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.exp2Stm;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
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
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;
import org.overture.codegen.vdm2c.utils.CTransUtil;
import org.overture.codegen.vdm2c.utils.GlobalFieldUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldIdentifierToFieldGetApplyTrans extends
DepthFirstAnalysisCAdaptor
{
	final static Logger logger = LoggerFactory.getLogger(FieldIdentifierToFieldGetApplyTrans.class);
	public TransAssistantIR assist;
	final GlobalFieldUtil fieldUtil;

	final static String fieldPrefix = "field_tmp_";

	public FieldIdentifierToFieldGetApplyTrans(TransAssistantIR assist)
	{
		this.assist = assist;
		this.fieldUtil = new GlobalFieldUtil(assist);
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
			fieldClassName = thisClassName; // default to same class

			if (varExp.getVardef() instanceof AInheritedDefinition)
			{
				AInheritedDefinition idef = (AInheritedDefinition) varExp.getVardef();
				fieldClassName = idef.getClassDefinition().getName().getName();
			}

			PDefinition vardef = CTransUtil.unwrapInheritedDef(varExp.getVardef());

			if (vardef instanceof AInstanceVariableDefinition)
			{
				if (vardef.getAccess().getStatic() != null)
				{
					fieldUtil.replaceWithStaticReference(vardef.getClassDefinition(), node);
					return;
				}

			} else if (vardef instanceof ALocalDefinition
					&& ((ALocalDefinition) vardef).getValueDefinition())
			{
				if (vardef.getAccess().getStatic() != null)
				{
					fieldUtil.replaceWithStaticReference(vardef.getClassDefinition(), node);
					return;
				}
				return;
			}

		} else if (vdmNode instanceof AIdentifierStateDesignator)
		{
			AIdentifierStateDesignator designator = (AIdentifierStateDesignator) vdmNode;

			// why is the definition not kept here? we dont know what this points to

			AClassClassDefinition thisClass = designator.getAncestor(AClassClassDefinition.class);// the containing
			// class
			thisClassName = thisClass.getName().getName();

			// while (thisClass != null && fieldClassName == null)
			{
				List<PDefinition> definitons = new Vector<PDefinition>();
				definitons.addAll(thisClass.getDefinitions());
				definitons.addAll(thisClass.getAllInheritedDefinitions());
				for (PDefinition def : definitons)
				{
					if (def instanceof AInheritedDefinition)
					{
						def = ((AInheritedDefinition) def).getSuperdef();
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
				logger.warn("Field class name not found: {}", node);
			}
		}

		if (thisClassName != null && fieldClassName != null)
		{

			AMacroApplyExpIR apply = newMacroApply(GET_FIELD_PTR);
			assist.replaceNodeWith(node, apply);

			// add this type
			apply.getArgs().add(createIdentifier(thisClassName, node.getSourceNode()));
			// add field owner type
			apply.getArgs().add(createIdentifier(fieldClassName, node.getSourceNode()));
			// add this
			apply.getArgs().add(createIdentifier("this", node.getSourceNode()));
			// add field name
			apply.getArgs().add(node);
			apply.setType(node.getType());
		}
	}

	@Override
	public void caseAAssignToExpStmIR(AAssignToExpStmIR node)
			throws AnalysisException
	{
		if (node.getTarget() instanceof AIdentifierVarExpIR
				&& ((AIdentifierVarExpIR) node.getTarget()).getIsLocal())
		{
			// if not handle here then process children
			super.caseAAssignToExpStmIR(node);
			return;
		}

		if(node.getTarget() instanceof AIdentifierVarExpIR)
		{
			AIdentifierVarExpIR target = (AIdentifierVarExpIR) node.getTarget();

			// class

			SClassDeclIR cDef = node.getAncestor(SClassDeclIR.class);
			if (fieldUtil.isStatic(cDef, target.getName()))
			{
				AFieldDeclIR field = fieldUtil.lookupField(cDef, target.getName());
				AIdentifierVarExpIR id = createIdentifier(field.getName(), target.getSourceNode());
				id.setType(target.getType().clone());
				assist.replaceNodeWith(node.getTarget(), id);
				return;
			}
			AClassClassDefinition classDef = target.getSourceNode().getVdmNode().getAncestor(AClassClassDefinition.class);

			String thisClassName = classDef.getName().getName();// the
			// containing
			// field owner
			String fieldClassName = fieldUtil.lookupFieldClass(target.getAncestor(ADefaultClassDeclIR.class), target.getName());

			String name = assist.getInfo().getTempVarNameGen().nextVarName(fieldPrefix);

			// process right side of assignment
			node.getExp().apply(THIS);
			AVarDeclIR retVar = newDeclarationAssignment(name, node.getExp().getType().clone(), newApply("vdmClone", node.getExp().clone()), node.getExp().getSourceNode());

			ABlockStmIR replBlock = new ABlockStmIR();
			replBlock.setScoped(true);
			replBlock.getLocalDefs().add(retVar);

			assist.replaceNodeWith(node, replBlock);

			AMacroApplyExpIR apply = newMacroApply(SET_FIELD_PTR);// new AApplyExpIR();
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
		else if(node.getTarget() instanceof AExplicitVarExpIR)
		{
			AExplicitVarExpIR target = (AExplicitVarExpIR) node.getTarget();

			// class

			SClassDeclIR cDef = node.getAncestor(SClassDeclIR.class);
			/*
			if (fieldUtil.isStatic(cDef, target.getName()))
			{
				AFieldDeclIR field = fieldUtil.lookupField(cDef, target.getName());
				AIdentifierVarExpIR id = createIdentifier(field.getName(), target.getSourceNode());
				id.setType(target.getType().clone());
				assist.replaceNodeWith(node.getTarget(), id);
				return;
			}
			*/
			AClassClassDefinition classDef = target.getSourceNode().getVdmNode().getAncestor(AClassClassDefinition.class);

			String thisClassName = classDef.getName().getName();// the
			// containing
			// field owner
			String fieldClassName = fieldUtil.lookupFieldClass(target.getAncestor(ADefaultClassDeclIR.class), target.getName());

			String name = assist.getInfo().getTempVarNameGen().nextVarName(fieldPrefix);

			// process right side of assignment
			node.getExp().apply(THIS);
			AVarDeclIR retVar = newDeclarationAssignment(name, node.getExp().getType().clone(), newApply("vdmClone", node.getExp().clone()), node.getExp().getSourceNode());

			ABlockStmIR replBlock = new ABlockStmIR();
			replBlock.setScoped(true);
			replBlock.getLocalDefs().add(retVar);

			assist.replaceNodeWith(node, replBlock);

			AMacroApplyExpIR apply = newMacroApply(SET_FIELD_PTR);// new AApplyExpIR();
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
}