package org.overture.codegen.vdm2c.transformations;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SFunctionDefinitionBase;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.ast.statements.AApplyObjectDesignator;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeComparator;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;

public class CompatibleMethodCollector
{
	public List<PDefinition> collectCompatibleMethods(
			SClassDefinition classDef, String unbMangledMethodName, INode call,
			List<PType> methodArgTypes)
	{
		List<PDefinition> methods = new Vector<PDefinition>();

		for (PDefinition def : classDef.getDefinitions())
		{
			if (def instanceof SFunctionDefinition
					|| def instanceof SOperationDefinition)
			{
				if (def.getName().getName().equals(unbMangledMethodName))
				{
					List<PType> argTypes = getArgTypes(def);
					List<PType> callArgTypes = methodArgTypes;// getArgTypes(call);

					TypeComparator tcomp = new TypeComparator(new TypeCheckerAssistantFactory());
					if (tcomp.compatible(argTypes, callArgTypes))
					{
						methods.add(def);
					}
				}
			}
		}

		for (SClassDefinition superDef : classDef.getSuperDefs())
		{
			methods.addAll(collectCompatibleMethods(superDef, unbMangledMethodName, call, methodArgTypes));
		}

		return methods;
	}

	public List<PType> getArgTypes(INode node)
	{
		LinkedList<PExp> cargs = null;

		if (node instanceof SFunctionDefinitionBase)
		{
			AFunctionType ft = (AFunctionType) ((SFunctionDefinitionBase) node).getType();
			return ft.getParameters();
		} else if (node instanceof SOperationDefinition)
		{
			AOperationType ft = (AOperationType) ((SOperationDefinition) node).getType();
			return ft.getParameters();
		} else if (node instanceof ACallStm)
		{
			cargs = ((ACallStm) node).getArgs();

		} else if (node instanceof AApplyExp)
		{
			cargs = ((AApplyExp) node).getArgs();

		} else if (node instanceof ACallObjectStm)
		{
			cargs = ((ACallObjectStm) node).getArgs();

		} else if (node instanceof AApplyObjectDesignator)
		{
			cargs = ((AApplyObjectDesignator) node).getArgs();
		}

		if (cargs != null)
		{
			List<PType> argTypes = new Vector<PType>();
			for (PExp pExp : cargs)
			{
				argTypes.add(pExp.getType());
			}
			return argTypes;
		}

		return new LinkedList<PType>();
	}
}