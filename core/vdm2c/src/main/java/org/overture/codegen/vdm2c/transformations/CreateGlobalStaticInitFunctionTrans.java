package org.overture.codegen.vdm2c.transformations;

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
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.types.AVoidTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class CreateGlobalStaticInitFunctionTrans extends
		DepthFirstAnalysisCAdaptor
{
	private static final String GLOBAL_STATIC_INIT_FUNCTION_PATTERN = "%s_static_init";
	private static final String GLOBAL_STATIC_SHUTDOWN_FUNCTION_PATTERN = "%s_static_shutdown";
	public TransAssistantIR assist;

	public CreateGlobalStaticInitFunctionTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	void createInitMethod(ADefaultClassDeclIR node) throws AnalysisException
	{
		ABlockStmIR body = new ABlockStmIR();

		for (AFieldDeclIR field : node.getFields())
		{
			if (!field.getFinal() && field.getStatic()
					&& field.getInitial() != null)
			{
				body.getStatements().add(newAssignment(newIdentifier(field.getName(), null), field.getInitial()));
			}
		}

		//In case there is nothing to initialize, we still want the functions to have a body.  See comment below.
		body.getStatements().add(new AReturnStmIR());
				
		//Emit init function even if no static fields are present.  Simplifies FMU export.
		AMethodDeclIR method = newInternalMethod(String.format(GLOBAL_STATIC_INIT_FUNCTION_PATTERN, node.getName()), body, new AVoidTypeIR(), false);
		method.setAccess("public");
		node.getMethods().add(method);
	}

	void createShutdownMethod(ADefaultClassDeclIR node)
			throws AnalysisException
	{
		ABlockStmIR body = new ABlockStmIR();

		for (AFieldDeclIR field : node.getFields())
		{
			if (!field.getFinal() && field.getStatic())
			{
				body.getStatements().add(toStm(ValueSemantics.free(field.getName(), field.getSourceNode())));
			}
		}

		//In case there is nothing to initialize, we still want the functions to have a body.  See comment below.
		body.getStatements().add(new AReturnStmIR());
				
		//Emit shutdown function even if no static fields are present.  Simplifies FMU export.
		AMethodDeclIR method = newInternalMethod(String.format(GLOBAL_STATIC_SHUTDOWN_FUNCTION_PATTERN, node.getName()), body, new AVoidTypeIR(), false);
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