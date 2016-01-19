package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.createIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.exp2Stm;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newMacroApply;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.SClassDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.ACallObjectStmCG;
import org.overture.codegen.cgast.statements.APlainCallStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class CallRewriteTrans extends DepthFirstAnalysisAdaptor
{
	public TransAssistantCG assist;

	final CompatibleMethodCollector methodCollector = new CompatibleMethodCollector();
	

	public CallRewriteTrans(TransAssistantCG assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseAReturnStmCG(AReturnStmCG node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseAReturnStmCG(node);
	}

	@Override
	public void caseAPlainCallStmCG(APlainCallStmCG node)
			throws AnalysisException
	{
		// op(a,d,f); --no root, so current class is the root.

		SClassDeclCG cDef = node.getAncestor(SClassDeclCG.class);
		List<PDefinition> tmp = methodCollector.collectCompatibleMethods((SClassDefinition) cDef.getSourceNode().getVdmNode(), node.getName(), node.getSourceNode().getVdmNode(), methodCollector.getArgTypes(node.getSourceNode().getVdmNode()));

		List<AMethodDeclCG> resolvedMethods = lookupVdmFunOpToMethods(tmp);

		//FIXME: we need to consider all methods not only the first one
		AMethodDeclCG selectedMethos = resolvedMethods.get(0);
		String thisType = cDef.getName();
		String methodOwnerType = selectedMethos.getAncestor(SClassDeclCG.class).getName();
		String thisArgs = "this";
		String methodId = String.format("CLASS_%s_%s", methodOwnerType, selectedMethos.getName());

		SStmCG apple =exp2Stm( newMacroApply("CALL_FUNC_PTR",  createIdentifier(thisType, null), createIdentifier(methodOwnerType, null), createIdentifier(thisArgs, null),createIdentifier(methodId, null)));

		assist.replaceNodeWith(node, apple);

	}

	List<AMethodDeclCG> lookupVdmFunOpToMethods(List<PDefinition> funOps)
			throws AnalysisException
	{
		List<AMethodDeclCG> methods = new Vector<AMethodDeclCG>();

		// must be ordered so run over vdm defs
		for (PDefinition def : funOps)
		{
			for (SClassDeclCG cgClass : assist.getInfo().getClasses())
			{
				for (AMethodDeclCG m : cgClass.getMethods())
				{
					if (!m.getIsConstructor()
							&& m.getSourceNode().getVdmNode() == def)
					{
						methods.add(m);
					}
				}
			}
		}

		if (methods.size() != funOps.size())
		{
			throw new AnalysisException("Not all functions or operations was resolved");
		}

		return methods;
	}



	@Override
	public void caseAApplyExpCG(AApplyExpCG node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseAApplyExpCG(node);
	}

	@Override
	public void caseACallObjectExpStmCG(ACallObjectExpStmCG node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseACallObjectExpStmCG(node);
	}

	@Override
	public void caseACallObjectStmCG(ACallObjectStmCG node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseACallObjectStmCG(node);
	}
}