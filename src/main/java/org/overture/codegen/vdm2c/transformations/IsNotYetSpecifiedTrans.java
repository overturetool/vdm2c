package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newInternalMethod;
import static org.overture.codegen.vdm2c.utils.CTransUtil.toStm;

import java.util.List;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.ANotImplementedExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.ANotImplementedStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.Vdm2cTag;
import org.overture.codegen.vdm2c.utils.NameMangler;

public class IsNotYetSpecifiedTrans extends DepthFirstAnalysisCAdaptor
{
	private final static String externalMethodName = "vdm_%s_%s";
	public TransAssistantIR assist;

	public IsNotYetSpecifiedTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@SuppressWarnings("unchecked")
	private void rewrite(INode node) throws AnalysisException
	{
		SClassDeclIR classDef = node.getAncestor(SClassDeclIR.class);
		AMethodDeclIR method = node.getAncestor(AMethodDeclIR.class);

		String name = String.format(externalMethodName, classDef.getName(), NameMangler.getName(method.getName()));
		AMethodDeclIR externMethod = newInternalMethod(name, null, method.getMethodType().getResult().clone());
		externMethod.setFormalParams((List<? extends AFormalParamLocalParamIR>) method.getFormalParams().clone());
		// remove this
		externMethod.getFormalParams().remove(0);

		externMethod.setName(name);
		externMethod.setAccess("public");
		((Vdm2cTag) externMethod.getTag()).methodTags.add(Vdm2cTag.MethodTag.HeaderOnly);

		classDef.getMethods().add(0, externMethod);
		AApplyExpIR apply = newApply(externMethod.getName());
		for (AFormalParamLocalParamIR formal : method.getFormalParams())
		{
			if (formal.getPattern() instanceof AIdentifierPatternIR)
			{
				apply.getArgs().add(newIdentifier(((AIdentifierPatternIR) formal.getPattern()).getName(), formal.getSourceNode()));
			} else
			{
				throw new AnalysisException("Found not supported pattern in call to external 'is not yet specified' method");
			}
		}
		// remove this
		apply.getArgs().remove(0);

		// handle both functions and operations
		SStmIR stm = null;
		stm = toStm(apply);
		assist.replaceNodeWith(node, stm);
	}

	@Override
	public void caseANotImplementedExpIR(ANotImplementedExpIR node)
			throws AnalysisException
	{
		rewrite(node);
	}

	@Override
	public void caseANotImplementedStmIR(ANotImplementedStmIR node)
			throws AnalysisException
	{
		rewrite(node);
	}
}