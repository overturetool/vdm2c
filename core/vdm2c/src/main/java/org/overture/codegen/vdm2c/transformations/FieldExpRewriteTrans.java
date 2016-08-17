package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.SET_FIELD;
import static org.overture.codegen.vdm2c.utils.CTransUtil.createIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newDeclarationAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newMacroApply;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.AFieldExpIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.AExpStmIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;
import org.overture.codegen.vdm2c.utils.GlobalFieldUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kel
 */
public class FieldExpRewriteTrans extends DepthFirstAnalysisCAdaptor
{
	final static Logger logger = LoggerFactory.getLogger(FieldExpRewriteTrans.class);
	public TransAssistantIR assist;

	final CompatibleMethodCollector methodCollector = new CompatibleMethodCollector();
	final GlobalFieldUtil fieldUtil;

	final static String retPrefix = "fieldTmp_";

	public FieldExpRewriteTrans(TransAssistantIR assist)
	{
		this.assist = assist;
		this.fieldUtil = new GlobalFieldUtil(assist);
	}
	
	@Override
	public void caseAAssignToExpStmIR(AAssignToExpStmIR node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseAAssignToExpStmIR(node);

		if(node.getTarget() instanceof AFieldExpIR)
		{
			String thisClassName = null;
			String fieldClassName = null;
			if (((AFieldExpIR)node.getTarget()).getObject().getType() instanceof AClassTypeIR)
			{
				AClassTypeIR classType = (AClassTypeIR) ((AFieldExpIR)node.getTarget()).getObject().getType();

				thisClassName = classType.getName();

				for (SClassDeclIR c : assist.getInfo().getClasses())
				{
					if (c.getName().equals(classType.getName()))
					{
						if (fieldUtil.isStatic(c, ((AFieldExpIR)node.getTarget()).getMemberName()))
						{
							fieldUtil.replaceWithStaticReference(c, ((AFieldExpIR)node.getTarget()).getMemberName(), ((AFieldExpIR)node.getTarget()));
							return;
						}

						fieldClassName = fieldUtil.lookupFieldClass(c, ((AFieldExpIR)node.getTarget()).getMemberName());

						if (thisClassName != null && fieldClassName != null)
						{
							// ok macro is protected so move object out in scope

							ABlockStmIR block = node.getAncestor(ABlockStmIR.class);

							if (block != null)
							{
								String fieldNameTmp = assist.getInfo().getTempVarNameGen().nextVarName(retPrefix);
								String expNameTmp = assist.getInfo().getTempVarNameGen().nextVarName(retPrefix);
								SourceNode objSource = ((AFieldExpIR)node.getTarget()).getObject().getSourceNode();
								block.getLocalDefs().add(newDeclarationAssignment(fieldNameTmp, ((AFieldExpIR)node.getTarget()).getObject().getType().clone(), ((AFieldExpIR)node.getTarget()).getObject(), objSource));
								block.getLocalDefs().add(newDeclarationAssignment(expNameTmp, node.getExp().getType().clone(), node.getExp().clone(), node.getExp().getSourceNode()));

								AMacroApplyExpIR apply = newMacroApply(SET_FIELD);
								AExpStmIR wrap = new AExpStmIR();
								wrap.setExp(apply);
								
								// add this type
								apply.getArgs().add(createIdentifier(thisClassName, node.getSourceNode()));
								// add field owner type
								apply.getArgs().add(createIdentifier(fieldClassName, node.getSourceNode()));
								// add this
								apply.getArgs().add(createIdentifier(fieldNameTmp, objSource));
								// add field name
								apply.getArgs().add(createIdentifier(((AFieldExpIR)node.getTarget()).getMemberName(), node.getSourceNode()));
								// add assigned expression
								apply.getArgs().add(node.getExp().clone());
								
								assist.replaceNodeWith(node, wrap);

								return;
							}
						}
					}
				}
			}
		}
	}

}