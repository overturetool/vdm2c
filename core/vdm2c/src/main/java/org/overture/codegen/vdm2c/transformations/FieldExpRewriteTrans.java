package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.GET_FIELD;
import static org.overture.codegen.vdm2c.utils.CTransUtil.createIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newDeclarationAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newMacroApply;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.AFieldExpIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
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
	public void caseAFieldExpIR(AFieldExpIR node) throws AnalysisException
	{
		super.caseAFieldExpIR(node);
		if (node.getType() instanceof AMethodTypeIR)
		{
			// handled in CallRewriteTrans
			return;
		} else
		{
			// its a field
			String thisClassName = null;
			String fieldClassName = null;
			if (node.getObject().getType() instanceof AClassTypeIR)
			{
				AClassTypeIR classType = (AClassTypeIR) node.getObject().getType();

				thisClassName = classType.getName();

				for (SClassDeclIR c : assist.getInfo().getClasses())
				{
					if (c.getName().equals(classType.getName()))
					{
						if (fieldUtil.isStatic(c, node.getMemberName()))
						{
							fieldUtil.replaceWithStaticReference(c, node.getMemberName(), node);
							return;
						}

						fieldClassName = fieldUtil.lookupFieldClass(c, node.getMemberName());

						if (thisClassName != null && fieldClassName != null)
						{

							// ok macro is protected so move object out in scope

							ABlockStmIR block = node.getAncestor(ABlockStmIR.class);

							if (block != null)
							{
								String fieldNameTmp = assist.getInfo().getTempVarNameGen().nextVarName(retPrefix);
								SourceNode objSource = node.getObject().getSourceNode();
								block.getLocalDefs().add(newDeclarationAssignment(fieldNameTmp, node.getObject().getType().clone(), node.getObject(), objSource));

								AMacroApplyExpIR apply = newMacroApply(GET_FIELD);

								assist.replaceNodeWith(node, apply);

								// add this type
								apply.getArgs().add(createIdentifier(thisClassName, node.getSourceNode()));
								// add field owner type
								apply.getArgs().add(createIdentifier(fieldClassName, node.getSourceNode()));
								// add this
								apply.getArgs().add(createIdentifier(fieldNameTmp, objSource));
								// add field name
								apply.getArgs().add(createIdentifier(node.getMemberName(), node.getSourceNode()));
								return;
							}
						}
					}
				}
			}
		}
		logger.error("AFieldExpIR not replaced: {}", node);
	}
}