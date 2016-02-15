package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.createIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.exp2Stm;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newMacroApply;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.types.AClassType;
import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.statements.ACallObjectExpStmIR;
import org.overture.codegen.ir.statements.ACallObjectStmIR;
import org.overture.codegen.ir.statements.APlainCallStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.ASeqSeqTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;

/**
 * See {@link https://github.com/overturetool/vdm2c/issues/1} for the full discussion on call semantics
 * 
 * @author kel
 */
public class CallRewriteTrans extends DepthFirstAnalysisCAdaptor
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
		if ((node.getClassType() + "").equals("IO"))
		{
			return;// FIXME handle external stuff
		}
		// op(a,d,f); --no root, so current class is the root.

		SClassDeclIR cDef = node.getAncestor(SClassDeclIR.class);
		List<PDefinition> tmp = methodCollector.collectCompatibleMethods((SClassDefinition) cDef.getSourceNode().getVdmNode(), node.getName(), node.getSourceNode().getVdmNode(), methodCollector.getArgTypes(node.getSourceNode().getVdmNode()));

		List<AMethodDeclIR> resolvedMethods = lookupVdmFunOpToMethods(tmp);

		// FIXME: we need to consider all methods not only the first one
		SStmIR apple = exp2Stm(createLocalPtrApply(resolvedMethods.get(0), cDef.getName(), node.getArgs()));
		apple.setSourceNode(node.getSourceNode());
		assist.replaceNodeWith(node, apple);

	}

	SExpIR createLocalPtrApply(AMethodDeclIR method, String thisName,
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

	SExpIR createClassApply(AMethodDeclIR method, String thisName,
			SExpIR classValue, List<SExpIR> linkedList)
			throws AnalysisException
	{
		AMethodDeclIR selectedMethod = method;
		String thisType = thisName;
		String methodOwnerType = selectedMethod.getAncestor(SClassDeclIR.class).getName();
		String methodId = String.format("CLASS_%s_%s", methodOwnerType, selectedMethod.getName());
		// CALL_FUNC(thisTypeName,funcTname,classValue,id, args...
		AMacroApplyExpIR apply = newMacroApply("CALL_FUNC", createIdentifier(thisType, null), createIdentifier(methodOwnerType, null), classValue, createIdentifier(methodId, null));

		for (SExpIR arg : linkedList)
		{
			arg.apply(THIS);
			apply.getArgs().add(arg.clone());
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
					if (!m.getIsConstructor() && m.getSourceNode() != null
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
				SClassDeclIR cDef = node.getAncestor(SClassDeclIR.class);
				List<PDefinition> tmp = methodCollector.collectCompatibleMethods((SClassDefinition) cDef.getSourceNode().getVdmNode(), name, node.getSourceNode().getVdmNode(), methodCollector.getArgTypes(node.getSourceNode().getVdmNode()));
				System.out.println();
				List<AMethodDeclIR> resolvedMethods = lookupVdmFunOpToMethods(tmp);
				if (resolvedMethods.isEmpty())
				{
					System.err.println("Generator error unable to find method");
					return;
				}
				SExpIR apply = createLocalPtrApply(resolvedMethods.get(0), cDef.getName(), node.getArgs());
				apply.setSourceNode(node.getSourceNode());
				assist.replaceNodeWith(node, apply);
			} else if (root.getType() instanceof ASeqSeqTypeIR)
			{
				// sequence index
				System.out.println();
				AApplyExpIR seqIndexApply = newApply("vdmSeqIndex", node.getRoot());
				seqIndexApply.getArgs().addAll(node.getArgs());
				seqIndexApply.setSourceNode(node.getSourceNode());
				seqIndexApply.setType(node.getType());

				assist.replaceNodeWith(node, seqIndexApply);
				seqIndexApply.apply(THIS);
			}
		}
	}

	@Override
	public void caseACallObjectExpStmIR(ACallObjectExpStmIR node)
			throws AnalysisException
	{
		super.caseACallObjectExpStmIR(node);

		INode objectVdmType = node.getObj().getType().getSourceNode().getVdmNode();

		SClassDefinition objectClass = ((AClassType) objectVdmType).getClassdef();

		List<PDefinition> tmp = methodCollector.collectCompatibleMethods(objectClass, node.getFieldName(), node.getSourceNode().getVdmNode(), methodCollector.getArgTypes(node.getSourceNode().getVdmNode()));

		List<AMethodDeclIR> resolvedMethods = lookupVdmFunOpToMethods(tmp);

		// CALL_FUNC(thisTypeName,funcTname,classValue,id, args...
		// FIXME: we need to consider all methods not only the first one
		SStmIR apple = exp2Stm(createClassApply(resolvedMethods.get(0), objectClass.getName().getName(), node.getObj(), node.getArgs()));
		apple.setSourceNode(node.getSourceNode());

		assist.replaceNodeWith(node, apple);

	}

	@Override
	public void caseACallObjectStmIR(ACallObjectStmIR node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseACallObjectStmIR(node);
	}
}