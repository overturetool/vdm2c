package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.createIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.exp2Stm;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newDeclarationAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newMacroApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newTvpType;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;
import org.overture.ast.types.AClassType;
import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.AFieldExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.ANullExpIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ACallObjectExpStmIR;
import org.overture.codegen.ir.statements.ACallObjectStmIR;
import org.overture.codegen.ir.statements.APlainCallStmIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.ASeqSeqTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;
import org.overture.codegen.vdm2c.utils.CTransUtil;
import org.overture.codegen.vdm2c.utils.NameMangler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * See {@link https://github.com/overturetool/vdm2c/issues/1} for the full discussion on call semantics
 * 
 * @author kel
 */
public class CallRewriteTrans extends DepthFirstAnalysisCAdaptor
{
	final static Logger logger = LoggerFactory.getLogger(CallRewriteTrans.class);
	public TransAssistantIR assist;

	final CompatibleMethodCollector methodCollector = new CompatibleMethodCollector();

	public CallRewriteTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}
	
	@Override
	public void caseAPlainCallStmIR(APlainCallStmIR node)
			throws AnalysisException
	{
		// op(a,d,f); --no root, so current class is the root.

		SClassDeclIR cDef = null;

		if (node.getClassType() instanceof AClassTypeIR)
		{
			cDef = CTransUtil.getClass(assist, ((AClassTypeIR) node.getClassType()).getName());
		} else
		{
			cDef = node.getAncestor(SClassDeclIR.class);
		}

		List<PDefinition> tmp = methodCollector.collectCompatibleMethods((SClassDefinition) cDef.getSourceNode().getVdmNode(), node.getName(), node.getSourceNode().getVdmNode(), methodCollector.getArgTypes(node.getSourceNode().getVdmNode()));

		List<AMethodDeclIR> resolvedMethods = lookupVdmFunOpToMethods(tmp);

		//Calls to static methods must be treated differently.
		if(node.getIsStatic())
		{
			SStmIR staticcall;
			String tmpVarName = assist.getInfo().getTempVarNameGen().nextVarName("TmpVar");
			
//			//Declare and initialize temporary object for class containing static method in static init function.
			AMethodDeclIR constr = new AMethodDeclIR();
			AMethodDeclIR enclosing = new AMethodDeclIR();
			org.overture.codegen.ir.INode tmpnode = node;
			
			//Find the constructor to call, get its name.
			for (AMethodDeclIR method : cDef.getMethods())
			{
				if (method.getIsConstructor())
				{
					constr = method;
					break;
				}
			}

			//Find enclosing method of the static call.
			//Handle case of static call outside of method.
			while(!(tmpnode instanceof AMethodDeclIR))
			{
				tmpnode = tmpnode.parent();
			}
			enclosing = (AMethodDeclIR)tmpnode;
			//Handle case where body is not a block, but just a single statement.
			((ABlockStmIR)enclosing.getBody()).getLocalDefs().add(newDeclarationAssignment(
						tmpVarName,
						newTvpType(),
						newApply(NameMangler.mangle(constr), new ANullExpIR()),
						null));
				
			//Call static function instead.
			//staticcall = exp2Stm(createLocalPtrApply(resolvedMethods.get(0), cDef.getName(), node.getArgs()));
			staticcall = exp2Stm(createClassApply(resolvedMethods.get(0), cDef.getName(), createIdentifier(tmpVarName, null), node.getArgs()));
//			//Free the temporary object.  should be taken care of by other transformations.
			
			//replace node.
			staticcall.setSourceNode(node.getSourceNode());
			assist.replaceNodeWith(node, staticcall);
		}
		else
		{
			// FIXME: we need to consider all methods not only the first one
			SStmIR apple = exp2Stm(createLocalPtrApply(resolvedMethods.get(0), cDef.getName(), node.getArgs()));
			apple.setSourceNode(node.getSourceNode());
			assist.replaceNodeWith(node, apple);
		}

	}

	AMacroApplyExpIR createLocalPtrApply(AMethodDeclIR method, String thisName,
			List<SExpIR> linkedList) throws AnalysisException
	{
		AMethodDeclIR selectedMethod = method;
		String thisType = thisName;
		String methodOwnerType = selectedMethod.getAncestor(SClassDeclIR.class).getName();
		String thisArgs = "this";
		String methodId = String.format(CTransUtil.METHOD_CALL_ID_PATTERN, methodOwnerType, selectedMethod.getName());

		AMacroApplyExpIR apply = newMacroApply(CTransUtil.CALL_FUNC_PTR, createIdentifier(thisType, null), createIdentifier(methodOwnerType, null), createIdentifier(thisArgs, null), createIdentifier(methodId, null));
		apply.setType(method.getMethodType().getResult().clone());
		for (SExpIR arg : linkedList)
		{
			SExpIR arg0 = arg.clone();
			arg0.apply(THIS);
			apply.getArgs().add(arg0);
		}
		return apply;
	}

	AMacroApplyExpIR createClassApply(AMethodDeclIR method, String thisName,
			SExpIR classValue, List<SExpIR> linkedList)
					throws AnalysisException
	{
		AMethodDeclIR selectedMethod = method;
		String thisType = thisName;
		String methodOwnerType = selectedMethod.getAncestor(SClassDeclIR.class).getName();
		String methodId = String.format(CTransUtil.METHOD_CALL_ID_PATTERN, methodOwnerType, selectedMethod.getName());
		// CALL_FUNC(thisTypeName,funcTname,classValue,id, args...
		AMacroApplyExpIR apply = newMacroApply(CTransUtil.CALL_FUNC, createIdentifier(thisType, null), createIdentifier(methodOwnerType, null), classValue, createIdentifier(methodId, null));
		apply.setType(method.getMethodType().getResult().clone());
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
					//Do not need to check whether a constructor is being called because Overture only allows constructors to call constructors.
					if (m.getSourceNode() != null && m.getSourceNode().getVdmNode() == def)
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
		SExpIR rootNode = node.getRoot();

		List<AMethodDeclIR> resolvedMethods;
		
		if (rootNode instanceof AIdentifierVarExpIR)
		{
			AIdentifierVarExpIR root = (AIdentifierVarExpIR) rootNode;
			replaceNonStaticApplyWithMacro(root.getType(), root.getName(), null, node);

		} else if (rootNode instanceof AFieldExpIR)
		{
			AFieldExpIR field = (AFieldExpIR) rootNode;
			replaceNonStaticApplyWithMacro(field.getType(), field.getMemberName(), field.getObject(), node);
		} else if (rootNode instanceof AExplicitVarExpIR)
		{
			//This is very similar to the static case for methods.  Refactor both out into functions.
			AExplicitVarExpIR root = (AExplicitVarExpIR) rootNode;
			AMacroApplyExpIR staticcall;
			List<PDefinition> tmp;
			String owningClassName = ((AVariableExp)((AExplicitVarExpIR) root).getSourceNode().getVdmNode()).getName().getModule();
			SClassDeclIR cDef = CTransUtil.getClass(assist, owningClassName);
			
			tmp = methodCollector.collectCompatibleMethods((SClassDefinition) cDef.getSourceNode().getVdmNode(), root.getName(), node.getSourceNode().getVdmNode(), methodCollector.getArgTypes(node.getSourceNode().getVdmNode()));
			resolvedMethods = lookupVdmFunOpToMethods(tmp);			
					
			String tmpVarName = assist.getInfo().getTempVarNameGen().nextVarName("TmpVar");
			
			//Declare and initialize temporary object for class containing static method in static init function.
			AMethodDeclIR constr = new AMethodDeclIR();
			AMethodDeclIR enclosing = new AMethodDeclIR();
			org.overture.codegen.ir.INode tmpnode = node;
			
			//Find the constructor to call, get its name.
			for (AMethodDeclIR method : cDef.getMethods())
			{
				if (method.getIsConstructor())
				{
					constr = method;
					break;
				}
			}

			//Find enclosing method of the static call.
			//Handle case of static call outside of method.
			while(!(tmpnode instanceof AMethodDeclIR))
			{
				//TODO:  Something before this leaves parent node null.
				tmpnode = tmpnode.parent();
			}
			enclosing = (AMethodDeclIR)tmpnode;
			
			//Handle case where body is not a block, but just a single statement.
			((ABlockStmIR)enclosing.getBody()).getLocalDefs().add(newDeclarationAssignment(
						tmpVarName,
						newTvpType(),
						newApply(NameMangler.mangle(constr), new ANullExpIR()),
						null));
				
			//Call static function instead.
			staticcall = createClassApply(resolvedMethods.get(0), cDef.getName(), createIdentifier(tmpVarName, null), node.getArgs());
			//Free the temporary object.  should be taken care of by other transformations.
			
			//replace node.
			staticcall.setSourceNode(node.getSourceNode());
			assist.replaceNodeWith(node, staticcall);		
					
		}
	}

	/**This was created not with static calls in mind.
	 * 
	 * @param applyType
	 * @param callName
	 * @param object
	 * @param originalApply
	 * @throws AnalysisException
	 */
	void replaceNonStaticApplyWithMacro(
								STypeIR applyType,
								String callName,
								SExpIR object,
								AApplyExpIR originalApply) throws AnalysisException
	{
		if (applyType instanceof AMethodTypeIR)
		{
			// this is a call
			String name = callName;
			SClassDeclIR cDef = null;

			//Applied function is inside the current class.
			if (object == null)
			{
				cDef = originalApply.getAncestor(SClassDeclIR.class);
			} else
			{
				if (object.getType() instanceof AClassTypeIR)
				{
					String owningClassName = ((AClassTypeIR) object.getType()).getName();
					cDef = CTransUtil.getClass(assist, owningClassName);
				} else
				{
					logger.error("unable to obtain class type for call: {}", originalApply);
				}
			}

			INode vdmNode = originalApply.getSourceNode().getVdmNode();
			List<PDefinition> tmp = methodCollector.collectCompatibleMethods((SClassDefinition) cDef.getSourceNode().getVdmNode(), name, vdmNode, methodCollector.getArgTypes(vdmNode));
			List<AMethodDeclIR> resolvedMethods = lookupVdmFunOpToMethods(tmp);
			
			//This is the error.
			if (resolvedMethods.isEmpty())
			{
				logger.error("Generator error unable to find method for: {}", originalApply);
				return;
			}
			AMacroApplyExpIR apply = null;

			if (object == null)
			{
				apply = createLocalPtrApply(resolvedMethods.get(0), cDef.getName(), originalApply.getArgs());

			} else
			{
				apply = createClassApply(resolvedMethods.get(0), cDef.getName(), object, originalApply.getArgs());

			}
			apply.setSourceNode(originalApply.getSourceNode());
			assist.replaceNodeWith(originalApply, apply);

			//if object call then apply transformation to object too
			if(object != null)
			{
				apply.getArgs().get(2).apply(THIS);
			}

			//apply transformation to all arguments
			for (int i = 4; i < apply.getArgs().size(); i++)
			{
				apply.getArgs().get(i).apply(THIS);
			}
		} else if (applyType instanceof ASeqSeqTypeIR)
		{
			// sequence index
			AApplyExpIR seqIndexApply = newApply("vdmSeqIndex", originalApply.getRoot());
			seqIndexApply.getArgs().addAll(originalApply.getArgs());
			seqIndexApply.setSourceNode(originalApply.getSourceNode());
			seqIndexApply.setType(originalApply.getType());

			assist.replaceNodeWith(originalApply, seqIndexApply);
			seqIndexApply.apply(THIS);
		}else
		{
			//no rewrite but run on args too
			for (SExpIR arg : originalApply.getArgs())
			{
				arg.apply(THIS);
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
		logger.error("Reached unhandled call rewrite: {}", node);
		super.caseACallObjectStmIR(node);
	}
}