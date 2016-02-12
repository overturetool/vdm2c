package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newIdentifier;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.AVoidTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.Vdm2cTag;
import org.overture.codegen.vdm2c.Vdm2cTag.MethodTag;

public class CreateGlobalConstInitFunctionTrans extends
		DepthFirstAnalysisCAdaptor
{
	private static final String GLOBAL_CONST_INIT_FUNCTION_PATTERN = "%s_constInit";
	public TransAssistantIR assist;

	public CreateGlobalConstInitFunctionTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseADefaultClassDeclIR(ADefaultClassDeclIR node)
			throws AnalysisException
	{
		ABlockStmIR body = new ABlockStmIR();

		for (AFieldDeclIR field : node.getFields())
		{
			if (field.getFinal() && field.getInitial() != null)
			{
				body.getStatements().add(newAssignment(newIdentifier(field.getName(), null), field.getInitial()));
			}
		}

		if (body.getStatements().isEmpty())
		{
			return;
		}

		AMethodDeclIR method = new AMethodDeclIR();
		method.setAbstract(false);
		method.setAsync(false);
		method.setImplicit(false);
		method.setStatic(false);
		method.setAccess("public");
		method.setIsConstructor(false);
		method.setTag(new Vdm2cTag().addMethodTag(MethodTag.Internal));
		method.setBody(body);
		AMethodTypeIR mtype = new AMethodTypeIR();
		mtype.setResult(new AVoidTypeIR());
		method.setMethodType(mtype);
		method.setName(String.format(GLOBAL_CONST_INIT_FUNCTION_PATTERN, node.getName()));
		node.getMethods().add(method);
	}

}