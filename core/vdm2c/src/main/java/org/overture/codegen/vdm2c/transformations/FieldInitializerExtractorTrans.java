package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newInternalMethod;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newReturnStm;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.Vdm2cTag;
import org.overture.codegen.vdm2c.Vdm2cTag.MethodTag;
import org.overture.codegen.vdm2c.utils.NameMangler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldInitializerExtractorTrans extends DepthFirstAnalysisCAdaptor
{
	private static final String THIS_PARAM = "this";
	private static final String THIS_ARG = "this_ptr";

	final static Logger logger = LoggerFactory.getLogger(FieldInitializerExtractorTrans.class);
	
	private static final String FIELD_INITIALIZER = "fieldInitializer";
	public TransAssistantIR assist;

	public FieldInitializerExtractorTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseAFieldDeclIR(AFieldDeclIR node) throws AnalysisException
	{
		SExpIR initial = node.getInitial();
		if (initial != null)
		{
			if (initial.getType() == null)
			{
				node.setInitial(null);
				return;
			}
			STypeIR type = initial.getType().clone();
			SStmIR body = newReturnStm(initial);

			AMethodDeclIR method = newInternalMethod(assist.getInfo().getTempVarNameGen().nextVarName(FIELD_INITIALIZER), body, type,true);
			Vdm2cTag.addMethodTag(method, MethodTag.FieldInitializer);
			
			SClassDeclIR enclosingClass = node.getAncestor(SClassDeclIR.class);
			
			AExternalTypeIR extType = null;
			
			if (!node.getStatic() && !node.getFinal()) {
				if (enclosingClass != null) {
					
					String paramTypeName = enclosingClass.getName() + "CLASS";
					extType = new AExternalTypeIR();
					extType.setName(paramTypeName);

					AFormalParamLocalParamIR param = new AFormalParamLocalParamIR();
					param.setType(extType.clone());
					param.setPattern(assist.getInfo().getPatternAssistant().consIdPattern(THIS_PARAM));

					method.getFormalParams().add(param);
				} else {
					logger.error("Could not find enclosing class for field: " + node);
				}
			}

			AApplyExpIR newApply = newApply(NameMangler.mangle(method));
			
			if(extType != null)
			{
				AIdentifierVarExpIR thisArg = assist.getInfo().getExpAssistant().consIdVar(THIS_ARG, extType);
				newApply.getArgs().add(thisArg);
			}
			
			node.setInitial(newApply);

			SClassDeclIR cls = node.getAncestor(SClassDeclIR.class);
			cls.getMethods().add(0, method);
		}
	}
}