package org.overture.codegen.vdm2c.transformations;

import org.apache.log4j.Logger;
import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.ASelfExpIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;
import org.overture.codegen.vdm2c.utils.CTransUtil;

public class SelfTrans extends DepthFirstAnalysisCAdaptor
{
	protected static Logger log = Logger.getLogger(SelfTrans.class.getName());
	
	public static final String SELF = "SELF";
	
	private TransAssistantIR assist;
	
	public SelfTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}
	
	@Override
	public void caseASelfExpIR(ASelfExpIR node) throws AnalysisException
	{
		String objName = findObjectName(node);
		
		if(objName != null)
		{
			AIdentifierVarExpIR objNameVar = assist.getInfo().getExpAssistant().consIdVar(objName, node.getType().clone());
			AMacroApplyExpIR selfMacro = CTransUtil.newMacroApply(SELF, objNameVar);
			assist.replaceNodeWith(node, selfMacro);
		}
	}
	
	public static String findObjectName(ASelfExpIR self)
	{
		AMethodDeclIR enclosingMethod = self.getAncestor(AMethodDeclIR.class);
		
		if(enclosingMethod == null)
		{
			log.error("Expected self to have an enclosing method");
			return null;
		}
		
		if(enclosingMethod.getFormalParams().isEmpty())
		{
			log.error("Expected method to have parameters");
			return null;
		}
		
		AFormalParamLocalParamIR firstParam = enclosingMethod.getFormalParams().getFirst();
		
		STypeIR type = firstParam.getType();
		
		if(!(type instanceof AExternalTypeIR))
		{
			log.error("Expected external type by now");
			return null;
		}
		
		AExternalTypeIR extType = (AExternalTypeIR) type;
		
		// We do it like this because the name is just constructed on the fly
		// rather than being mangled
		return extType.getName().replaceFirst("CLASS$", "");
	}
}
