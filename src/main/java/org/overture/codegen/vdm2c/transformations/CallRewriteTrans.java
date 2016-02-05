package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.createIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.exp2Stm;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newMacroApply;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.statements.ACallObjectExpStmIR;
import org.overture.codegen.ir.statements.ACallObjectStmIR;
import org.overture.codegen.ir.statements.APlainCallStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;

/**
 * See {@link https://github.com/overturetool/vdm2c/issues/1} for the full discussion on call semantics
 * 
 * @author kel
 */
public class CallRewriteTrans extends DepthFirstAnalysisAdaptor
{
	public TransAssistantIR assist;

	final CompatibleMethodCollector methodCollector = new CompatibleMethodCollector();

	public CallRewriteTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseAReturnStmIR(AReturnStmIR node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseAReturnStmIR(node);
	}

	@Override
	public void caseAPlainCallStmIR(APlainCallStmIR node)
			throws AnalysisException
	{
		// op(a,d,f); --no root, so current class is the root.

		SClassDeclIR cDef = node.getAncestor(SClassDeclIR.class);
		List<PDefinition> tmp = methodCollector.collectCompatibleMethods((SClassDefinition) cDef.getSourceNode().getVdmNode(), node.getName(), node.getSourceNode().getVdmNode(), methodCollector.getArgTypes(node.getSourceNode().getVdmNode()));

		List<AMethodDeclIR> resolvedMethods = lookupVdmFunOpToMethods(tmp);

		// FIXME: we need to consider all methods not only the first one
		SStmIR apple = exp2Stm(createApply(resolvedMethods.get(0), cDef.getName(), node.getArgs()));

		assist.replaceNodeWith(node, apple);

	}

	SExpIR createApply(AMethodDeclIR method, String thisName,
			List<SExpIR> linkedList) throws AnalysisException
	{
		AMethodDeclIR selectedMethod = method;
		String thisType = thisName;
		String methodOwnerType = selectedMethod.getAncestor(SClassDeclIR.class).getName();
		String thisArgs = "this";
		String methodId = String.format("CLASS_%s_%s", methodOwnerType, selectedMethod.getName());

		AMacroApplyExpIR apply = newMacroApply("CALL_FUNC_PTR", createIdentifier(thisType, null), createIdentifier(methodOwnerType, null), createIdentifier(thisArgs, null), createIdentifier(methodId, null));

		for (SExpIR arg : linkedList)
		{
			arg.apply(THIS);
			apply.getArgs().add(arg);
		}
		return apply;
	}

	List<AMethodDeclIR> lookupVdmFunOpToMethods(List<PDefinition> funOps)
			throws AnalysisException
	{
		List<AMethodDeclIR> methods = new Vector<AMethodDeclIR>();

		// must be ordered so run over vdm defs
		for (PDefinition def : funOps)
		{
			for (SClassDeclIR cgClass : assist.getInfo().getClasses())
			{
				for (AMethodDeclIR m : cgClass.getMethods())
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
	public void caseAApplyExpIR(AApplyExpIR node) throws AnalysisException
	{
		if (node.getRoot() instanceof AIdentifierVarExpIR)
		{
			AIdentifierVarExpIR root = (AIdentifierVarExpIR) node.getRoot();
			if (root.getType() instanceof AMethodTypeIR)
			{
				// this is a call
				String name = root.getName();
				System.out.println();
				SClassDeclIR cDef = node.getAncestor(SClassDeclIR.class);
				List<PDefinition> tmp = methodCollector.collectCompatibleMethods((SClassDefinition) cDef.getSourceNode().getVdmNode(), name, node.getSourceNode().getVdmNode(), methodCollector.getArgTypes(node.getSourceNode().getVdmNode()));
				System.out.println();
				List<AMethodDeclIR> resolvedMethods = lookupVdmFunOpToMethods(tmp);
				assist.replaceNodeWith(node, createApply(resolvedMethods.get(0), cDef.getName(), node.getArgs()));
			}
		}
	}

	@Override
	public void caseACallObjectExpStmIR(ACallObjectExpStmIR node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseACallObjectExpStmIR(node);
	}

	@Override
	public void caseACallObjectStmIR(ACallObjectStmIR node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseACallObjectStmIR(node);
	}
}