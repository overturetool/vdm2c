package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.createIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.exp2Stm;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newMacroApply;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.codegen.ir.SExpCG;
import org.overture.codegen.ir.SStmCG;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AMethodDeclCG;
import org.overture.codegen.ir.declarations.SClassDeclCG;
import org.overture.codegen.ir.expressions.AApplyExpCG;
import org.overture.codegen.ir.expressions.AIdentifierVarExpCG;
import org.overture.codegen.ir.statements.ACallObjectExpStmCG;
import org.overture.codegen.ir.statements.ACallObjectStmCG;
import org.overture.codegen.ir.statements.APlainCallStmCG;
import org.overture.codegen.ir.statements.AReturnStmCG;
import org.overture.codegen.ir.types.AMethodTypeCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpCG;

/**
 * See {@link https://github.com/overturetool/vdm2c/issues/1} for the full discussion on call semantics
 * 
 * @author kel
 */
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

		// FIXME: we need to consider all methods not only the first one
		SStmCG apple = exp2Stm(createApply(resolvedMethods.get(0), cDef.getName(), node.getArgs()));

		assist.replaceNodeWith(node, apple);

	}

	SExpCG createApply(AMethodDeclCG method, String thisName,
			List<SExpCG> linkedList) throws AnalysisException
	{
		AMethodDeclCG selectedMethod = method;
		String thisType = thisName;
		String methodOwnerType = selectedMethod.getAncestor(SClassDeclCG.class).getName();
		String thisArgs = "this";
		String methodId = String.format("CLASS_%s_%s", methodOwnerType, selectedMethod.getName());

		AMacroApplyExpCG apply = newMacroApply("CALL_FUNC_PTR", createIdentifier(thisType, null), createIdentifier(methodOwnerType, null), createIdentifier(thisArgs, null), createIdentifier(methodId, null));

		for (SExpCG arg : linkedList)
		{
			arg.apply(THIS);
			apply.getArgs().add(arg);
		}
		return apply;
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
		if (node.getRoot() instanceof AIdentifierVarExpCG)
		{
			AIdentifierVarExpCG root = (AIdentifierVarExpCG) node.getRoot();
			if (root.getType() instanceof AMethodTypeCG)
			{
				// this is a call
				String name = root.getName();
				System.out.println();
				SClassDeclCG cDef = node.getAncestor(SClassDeclCG.class);
				List<PDefinition> tmp = methodCollector.collectCompatibleMethods((SClassDefinition) cDef.getSourceNode().getVdmNode(), name, node.getSourceNode().getVdmNode(), methodCollector.getArgTypes(node.getSourceNode().getVdmNode()));
				System.out.println();
				List<AMethodDeclCG> resolvedMethods = lookupVdmFunOpToMethods(tmp);
				assist.replaceNodeWith(node, createApply(resolvedMethods.get(0), cDef.getName(), node.getArgs()));
			}
		}
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