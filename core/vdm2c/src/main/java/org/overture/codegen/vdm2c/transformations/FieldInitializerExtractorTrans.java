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
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.Vdm2cTag;
import org.overture.codegen.vdm2c.Vdm2cTag.MethodTag;
import org.overture.codegen.vdm2c.utils.NameMangler;

public class FieldInitializerExtractorTrans extends DepthFirstAnalysisCAdaptor
{
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

			node.setInitial(newApply(NameMangler.mangle(method)));

			SClassDeclIR cls = node.getAncestor(SClassDeclIR.class);
			cls.getMethods().add(0, method);
		}
	}
}