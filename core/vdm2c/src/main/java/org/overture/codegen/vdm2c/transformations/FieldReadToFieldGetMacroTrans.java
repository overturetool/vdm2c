package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.GET_FIELD_PTR;
import static org.overture.codegen.vdm2c.utils.CTransUtil.createIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newMacroApply;

import org.overture.ast.definitions.SClassDefinitionBase;
import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.AFieldExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.AFieldStateDesignatorIR;
import org.overture.codegen.ir.statements.AIdentifierStateDesignatorIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;
import org.overture.codegen.vdm2c.utils.GlobalFieldUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//Transforms read references to fields to GET macros.
//TODO:  Should match on something more specific than just caseAIdentifierVarExpIR?
public class FieldReadToFieldGetMacroTrans extends
DepthFirstAnalysisCAdaptor
{
	final static Logger logger = LoggerFactory.getLogger(FieldReadToFieldGetMacroTrans.class);
	public TransAssistantIR assist;
	final GlobalFieldUtil fieldUtil;

	final static String fieldPrefix = "field_tmp_";

	public FieldReadToFieldGetMacroTrans(TransAssistantIR assist)
	{
		this.assist = assist;
		this.fieldUtil = new GlobalFieldUtil(assist);
	}


	//The case obj.field where the public field of an instance of an external class is read.
	@Override
	public void caseAFieldExpIR(AFieldExpIR node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseAFieldExpIR(node);
		
		if(node.parent() instanceof AAssignToExpStmIR &&
				((AAssignToExpStmIR)node.parent()).getTarget() == node)
		{
			//Current field is being assigned to, handled elsewhere.
			return;
		}

		//The remainder of the transformation does not yet deal with inherited field definitions.
//		String thisClassName = node.getAncestor(SClassDeclIR.class).getName();
		String fieldClassName = null;
		AFieldExpIR tmpnode = node.clone();
		
		
		
		

		for(SClassDeclIR c : assist.getInfo().getClasses())
		{
			if(fieldUtil.lookupField(c,  node.getMemberName()) != null)
			{
				fieldClassName = fieldUtil.lookupFieldClass(c, node.getMemberName());
			}
		}

		AMacroApplyExpIR apply = newMacroApply(GET_FIELD_PTR);
		assist.replaceNodeWith(node, apply);

		// add this type
		apply.getArgs().add(createIdentifier(fieldClassName, tmpnode.getSourceNode()));
		// add field owner type
		apply.getArgs().add(createIdentifier(fieldClassName, tmpnode.getSourceNode()));
		// add this
		apply.getArgs().add(createIdentifier("this", tmpnode.getSourceNode()));
		// add field name
		apply.getArgs().add(createIdentifier(tmpnode.getMemberName(), tmpnode.getSourceNode()));
		apply.setType(node.getType());




//		if (vdmNode instanceof AVariableExp)
//		{
//			AVariableExp varExp = (AVariableExp) vdmNode;
//
//			if (varExp.getType() instanceof AFunctionType
//					|| varExp.getType() instanceof AOperationType)
//			{
//				return;
//			}
//
//			thisClassName = varExp.getAncestor(AClassClassDefinition.class).getName().getName();// the containing
//			// class
//			fieldClassName = thisClassName; // default to same class
//
//			if (varExp.getVardef() instanceof AInheritedDefinition)
//			{
//				AInheritedDefinition idef = (AInheritedDefinition) varExp.getVardef();
//				fieldClassName = idef.getClassDefinition().getName().getName();
//			}
//
//			PDefinition vardef = CTransUtil.unwrapInheritedDef(varExp.getVardef());
//
//			//			if 
//
//		} else if (vdmNode instanceof AIdentifierStateDesignator)
//		{
//			AIdentifierStateDesignator designator = (AIdentifierStateDesignator) vdmNode;
//
//			// why is the definition not kept here? we dont know what this points to
//
//
//			SClassDefinitionBase thisClass = designator.getAncestor(AClassClassDefinition.class);
//
//			if(thisClass == null)
//			{
//				//Must be a system class.
//				thisClass = designator.getAncestor(ASystemClassDefinition.class);
//			}
//
//
//			// the containing class
//			thisClassName = thisClass.getName().getName();
//
//			// while (thisClass != null && fieldClassName == null)
//			{
//				List<PDefinition> definitons = new Vector<PDefinition>();
//				definitons.addAll(thisClass.getDefinitions());
//				definitons.addAll(thisClass.getAllInheritedDefinitions());
//				for (PDefinition def : definitons)
//				{
//					if (def instanceof AInheritedDefinition)
//					{
//						def = ((AInheritedDefinition) def).getSuperdef();
//					}
//
//					if (def instanceof AInstanceVariableDefinition)
//					{
//						if (def.getName().getName().equals(designator.getName().getName()))
//						{
//							fieldClassName = def.getClassDefinition().getName().getName();
//							break;
//						}
//					}
//				}
//			}
//
//			if (fieldClassName == null)
//			{
//				logger.warn("Field class name not found: {}", node);
//			}
//		}	
	}

	
	//The case field where a field local to this class is read.
	//Need to determine whether the identifier expression refers to a field or to some other variable.  Can be done by searchgin through
	//the fields of the class, or by using !isLocal().
		@Override
		public void caseAIdentifierVarExpIR(AIdentifierVarExpIR node)
				throws AnalysisException
		{
			super.caseAIdentifierVarExpIR(node);
			
			String thisClassName = null;
			String fieldClassName = null;
			
			if(node.getIsLocal())
			{
				//This identifier is not a field.
				return;
			}
			
			if(node.parent() instanceof AAssignToExpStmIR &&
					((AAssignToExpStmIR)node.parent()).getTarget() == node)
			{
				//Current field is being assigned to, handled elsewhere.
				return;
			}
			
			if(node.parent() instanceof AFieldExpIR)
			{
				return;
			}
			
			if(node.parent() instanceof AExplicitVarExpIR)
			{
				return;
			}
			
			thisClassName = node.getSourceNode().getVdmNode().getAncestor(SClassDefinitionBase.class).getName().getName();
			fieldClassName = thisClassName; // default to same class

			//Static fields are turned into global variables, so they are accessed differently.
			for(SClassDeclIR c : assist.getInfo().getClasses())
			{
				if(c.getName().equals(thisClassName))
				{
					if(fieldUtil.lookupField(c,  node.getName()) != null)
					{
						if(fieldUtil.lookupField(c,  node.getName()).getStatic())
						{
							return;
						}
					}
				}
			}

			
			
			
			
	
//				if (varExp.getVardef() instanceof AInheritedDefinition)
//				{
//					AInheritedDefinition idef = (AInheritedDefinition) varExp.getVardef();
//					fieldClassName = idef.getClassDefinition().getName().getName();
//				}
//	
//				PDefinition vardef = CTransUtil.unwrapInheritedDef(varExp.getVardef());
//	
//				if (vardef instanceof AInstanceVariableDefinition)
//				{
//					if (vardef.getAccess().getStatic() != null)
//					{
//						fieldUtil.replaceWithStaticReference(vardef.getClassDefinition(), node);
//						return;
//					}
//	
//				} else if (vardef instanceof ALocalDefinition
//						&& ((ALocalDefinition) vardef).getValueDefinition())
//				{
//					if (vardef.getAccess().getStatic() != null)
//					{
//						fieldUtil.replaceWithStaticReference(vardef.getClassDefinition(), node);
//						return;
//					}
//					return;
//				}
//	
			
//				else if (vdmNode instanceof AIdentifierStateDesignator)
//			{
//				AIdentifierStateDesignator designator = (AIdentifierStateDesignator) vdmNode;
//	
//				// why is the definition not kept here? we dont know what this points to
//	
//				
//				SClassDefinitionBase thisClass = designator.getAncestor(AClassClassDefinition.class);
//				
//				if(thisClass == null)
//				{
//					//Must be a system class.
//					thisClass = designator.getAncestor(ASystemClassDefinition.class);
//				}
//								
//				 
//				 // the containing class
//				thisClassName = thisClass.getName().getName();
//	
//				// while (thisClass != null && fieldClassName == null)
//				{
//					List<PDefinition> definitons = new Vector<PDefinition>();
//					definitons.addAll(thisClass.getDefinitions());
//					definitons.addAll(thisClass.getAllInheritedDefinitions());
//					for (PDefinition def : definitons)
//					{
//						if (def instanceof AInheritedDefinition)
//						{
//							def = ((AInheritedDefinition) def).getSuperdef();
//						}
//	
//						if (def instanceof AInstanceVariableDefinition)
//						{
//							if (def.getName().getName().equals(designator.getName().getName()))
//							{
//								fieldClassName = def.getClassDefinition().getName().getName();
//								break;
//							}
//						}
//					}
//				}
//	
//				if (fieldClassName == null)
//				{
//					logger.warn("Field class name not found: {}", node);
//				}
//			}
//	
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
}