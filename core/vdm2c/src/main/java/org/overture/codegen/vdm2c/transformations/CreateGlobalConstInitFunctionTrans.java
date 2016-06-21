package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newInternalMethod;
import static org.overture.codegen.vdm2c.utils.CTransUtil.toStm;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.types.AVoidTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class CreateGlobalConstInitFunctionTrans extends
		DepthFirstAnalysisCAdaptor
{
	private static final String GLOBAL_CONST_INIT_FUNCTION_PATTERN = "%s_const_init";
	private static final String GLOBAL_CONST_SHUTDOWN_FUNCTION_PATTERN = "%s_const_shutdown";
	public TransAssistantIR assist;

	public CreateGlobalConstInitFunctionTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	void createInitMethod(ADefaultClassDeclIR node) throws AnalysisException
	{
		ABlockStmIR body = new ABlockStmIR();

		for (AFieldDeclIR field : node.getFields())
		{
			if (field.getFinal() && field.getInitial() != null)
			{
				body.getStatements().add(newAssignment(newIdentifier(field.getName(), null), field.getInitial()));
			}
		}

//		if (body.getStatements().isEmpty())
//		{
//			return;
//		}

		AMethodDeclIR method = newInternalMethod(String.format(GLOBAL_CONST_INIT_FUNCTION_PATTERN, node.getName()), body, new AVoidTypeIR(), false);
		method.setAccess("public");
		node.getMethods().add(method);
	}

	void createShutdownMethod(ADefaultClassDeclIR node)
			throws AnalysisException
	{
		ABlockStmIR body = new ABlockStmIR();

		for (AFieldDeclIR field : node.getFields())
		{
			if (field.getFinal())
			{
				body.getStatements().add(toStm(newApply("vdmFree", newIdentifier(field.getName(), null))));
			}
		}

//		if (body.getStatements().isEmpty())
//		{
//			return;
//		}

		AMethodDeclIR method = newInternalMethod(String.format(GLOBAL_CONST_SHUTDOWN_FUNCTION_PATTERN, node.getName()), body, new AVoidTypeIR(), false);
		method.setAccess("public");
		node.getMethods().add(method);
	}

	@Override
	public void caseADefaultClassDeclIR(ADefaultClassDeclIR node)
			throws AnalysisException
	{
		createInitMethod(node);
		createShutdownMethod(node);
	}

}