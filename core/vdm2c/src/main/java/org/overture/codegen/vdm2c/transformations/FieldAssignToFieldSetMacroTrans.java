package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.SET_FIELD_PTR;
import static org.overture.codegen.vdm2c.utils.CTransUtil.createIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.exp2Stm;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newDeclarationAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newMacroApply;

import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.cgc.extast.analysis.AnswerCAdaptor;
import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.AFieldExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;
import org.overture.codegen.vdm2c.utils.CTransUtil;
import org.overture.codegen.vdm2c.utils.GlobalFieldUtil;
import org.overture.codegen.vdm2c.utils.NameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//Transforms read references to fields to GET macros.
//TODO:  Should match on something more specific than just caseAIdentifierVarExpIR?
public class FieldAssignToFieldSetMacroTrans extends
DepthFirstAnalysisCAdaptor
{
	final static Logger logger = LoggerFactory.getLogger(FieldAssignToFieldSetMacroTrans.class);
	public TransAssistantIR assist;
	final GlobalFieldUtil fieldUtil;

	final static String fieldPrefix = "field_tmp_";

	public FieldAssignToFieldSetMacroTrans(TransAssistantIR assist)
	{
		this.assist = assist;
		this.fieldUtil = new GlobalFieldUtil(assist);
	}

	

	@Override
	public void caseAAssignToExpStmIR(AAssignToExpStmIR node)
			throws AnalysisException
	{
		if (node.getTarget() instanceof AIdentifierVarExpIR && ((AIdentifierVarExpIR) node.getTarget()).getIsLocal())
		{
			// if not handle here then process children
			super.caseAAssignToExpStmIR(node);
			return;
		}

		if(node.getTarget() instanceof AIdentifierVarExpIR)
		{
			AIdentifierVarExpIR target = (AIdentifierVarExpIR) node.getTarget();
			org.overture.ast.node.INode vdm = target.getSourceNode().getVdmNode();
			
			boolean staticSubject = false;
			PDefinition def = null;
			
			if(vdm instanceof AVariableExp)
			{
				def = ((AVariableExp) vdm).getVardef();
			}
			else if(vdm instanceof AIdentifierStateDesignator)
			{
				def = assist.getInfo().getIdStateDesignatorDefs().get(vdm);
			}
			
			if(def instanceof AInheritedDefinition)
			{
				def = ((AInheritedDefinition) def).getSuperdef();
			}
			
			staticSubject = (def instanceof AInstanceVariableDefinition)
					&& (((AInstanceVariableDefinition) def).getAccess().getStatic() != null);

			if (staticSubject)
			{
				AIdentifierVarExpIR id = createIdentifier(target.getName(), target.getSourceNode());
				id.setType(target.getType().clone());
				assist.replaceNodeWith(node.getTarget(), id);
				return;
			}
			
			SClassDeclIR classDef = node.getAncestor(SClassDeclIR.class);
			String thisClassName = null;

			if(classDef != null)
			{
				thisClassName = classDef.getName();// the containing field owner
			}

			String fieldClassName = fieldUtil.lookupFieldClass(target.getAncestor(ADefaultClassDeclIR.class), target.getName());

			String name = assist.getInfo().getTempVarNameGen().nextVarName(fieldPrefix);

			// process right side of assignment
			node.getExp().apply(THIS);
			AVarDeclIR retVar = newDeclarationAssignment(name, node.getExp().getType().clone(), node.getExp().clone(), node.getExp().getSourceNode());

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
		//I think these two cases should be made more generic for static function and operation calls, not only in the context of an assignment.
		else if(node.getTarget() instanceof AExplicitVarExpIR)
		{
			//Name of class containing the field being referenced.
			String fieldClassName = ((AIdentifierStateDesignator)((AExplicitVarExpIR)node.getTarget()).getSourceNode().getVdmNode()).getName().getModule();

			//This should be the target class, not the current node's class 
			SClassDeclIR cDef = CTransUtil.getClass(assist,  fieldClassName);			

			//This assumes that the field is in the current class.
			//			if (fieldUtil.isStatic(cDef, target.getName()))
			//			{
			//				AFieldDeclIR field = fieldUtil.lookupField(cDef, target.getName());
			//				AIdentifierVarExpIR id = createIdentifier(field.getName(), target.getSourceNode());
			//				id.setType(target.getType().clone());
			//				assist.replaceNodeWith(node.getTarget(), id);
			//				return;
			//			}			

			String name = assist.getInfo().getTempVarNameGen().nextVarName(fieldPrefix);

			// process right side of assignment
			node.getExp().apply(THIS);

			AVarDeclIR rightToTemp = newDeclarationAssignment(name, node.getExp().getType().clone(), node.getExp().clone(), node.getExp().getSourceNode());


			//The actual assignment to the static field.  The generator emits simple golbal variables for static fields.
			AAssignToExpStmIR staticFieldAssign = 
					newAssignment(newIdentifier(
							NameConverter.getCName(fieldUtil.lookupField(cDef, node.getTarget().toString())), null),
							newIdentifier(name, null));

			AVarDeclIR retVar = newDeclarationAssignment(name, node.getExp().getType().clone(), node.getExp().clone(), node.getExp().getSourceNode());

			ABlockStmIR replBlock = new ABlockStmIR();
			replBlock.setScoped(true);
			replBlock.getLocalDefs().add(rightToTemp);

			assist.replaceNodeWith(node, replBlock);

			replBlock.getStatements().add(staticFieldAssign);

			AApplyExpIR vdmFree = new AApplyExpIR();
			vdmFree.setRoot(createIdentifier("vdmFree", node.getSourceNode()));
			vdmFree.getArgs().add(createIdentifier(name, retVar.getSourceNode()));

			replBlock.getStatements().add(exp2Stm(vdmFree));
		}
		else if(node.getTarget() instanceof AFieldExpIR)
		{
			AFieldExpIR field = (AFieldExpIR) node.getTarget();
			
			//Name of class containing the field being referenced.
			String fieldClassName = field.getAncestor(SClassDeclIR.class).getName();

			//This should be the target class, not the current node's class 
			SClassDeclIR cDef = CTransUtil.getClass(assist,  fieldClassName);			

			//This assumes that the field is in the current class.
			//			if (fieldUtil.isStatic(cDef, target.getName()))
			//			{
			//				AFieldDeclIR field = fieldUtil.lookupField(cDef, target.getName());
			//				AIdentifierVarExpIR id = createIdentifier(field.getName(), target.getSourceNode());
			//				id.setType(target.getType().clone());
			//				assist.replaceNodeWith(node.getTarget(), id);
			//				return;
			//			}			

			String name = assist.getInfo().getTempVarNameGen().nextVarName(fieldPrefix);

			// process right side of assignment
			node.getExp().apply(THIS);

			AVarDeclIR rightToTemp = newDeclarationAssignment(name, node.getExp().getType().clone(), node.getExp().clone(), node.getExp().getSourceNode());


			//The actual assignment to the static field.  The generator emits simple golbal variables for static fields.
			
			String fieldName = getFieldName(field);
			
			AAssignToExpStmIR staticFieldAssign = 
					newAssignment(newIdentifier(
							NameConverter.getCName(fieldUtil.lookupField(cDef, fieldName)), null),
							newIdentifier(name, null));

			AVarDeclIR retVar = newDeclarationAssignment(name, node.getExp().getType().clone(), node.getExp().clone(), node.getExp().getSourceNode());

			ABlockStmIR replBlock = new ABlockStmIR();
			replBlock.setScoped(true);
			replBlock.getLocalDefs().add(rightToTemp);

			assist.replaceNodeWith(node, replBlock);

			replBlock.getStatements().add(staticFieldAssign);

			AApplyExpIR vdmFree = new AApplyExpIR();
			vdmFree.setRoot(createIdentifier("vdmFree", node.getSourceNode()));
			vdmFree.getArgs().add(createIdentifier(name, retVar.getSourceNode()));

			replBlock.getStatements().add(exp2Stm(vdmFree));
		}
	}



	private String getFieldName(AFieldExpIR field)
	{
		try
		{
			return field.apply(new FieldNameFinder());
		} catch (AnalysisException e)
		{
			logger.error("Could not find name of field");
			e.printStackTrace();
			return null;
		}
	}
	
	class FieldNameFinder extends AnswerCAdaptor<String>
	{
		@Override
		public String caseAFieldExpIR(AFieldExpIR node) throws AnalysisException
		{
			return node.getObject().apply(this);
		}
		
		@Override
		public String caseAIdentifierVarExpIR(AIdentifierVarExpIR node)
				throws AnalysisException
		{
			return node.getName();
		}

		@Override
		public String createNewReturnValue(INode arg0) throws AnalysisException
		{
			return null;
		}

		@Override
		public String createNewReturnValue(Object arg0) throws AnalysisException
		{
			return null;
		}
	}
	
	
}