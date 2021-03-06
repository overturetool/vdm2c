package org.overture.codegen.vdm2c.utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.ACastUnaryExpIR;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.AExternalExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.AIntLiteralExpIR;
import org.overture.codegen.ir.expressions.SVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.AExpStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.AUnknownTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.Vdm2cTag;
import org.overture.codegen.vdm2c.Vdm2cTag.MethodTag;
import org.overture.codegen.vdm2c.extast.expressions.AArrayIndexExpIR;
import org.overture.codegen.vdm2c.extast.expressions.ACExpIR;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;
import org.overture.codegen.vdm2c.extast.expressions.AParenExpIR;
import org.overture.codegen.vdm2c.extast.expressions.APtrDerefExpIR;
import org.overture.codegen.vdm2c.extast.expressions.AStmExpIR;
import org.overture.codegen.vdm2c.extast.statements.ALocalVariableDeclarationStmIR;

public class CTransUtil
{
	public static final String GET_FIELD_PTR = "GET_FIELD_PTR";
	public static final String SET_FIELD_PTR = "SET_FIELD_PTR";
	public static final String GET_FIELD_PTR_BYREF = "GET_FIELD_PTR_BYREF";

	public static final String GET_FIELD = "GET_FIELD";
	public static final String SET_FIELD = "SET_FIELD";

	public static final String METHOD_CALL_ID_PATTERN = "CLASS_%s_%s";
	public static final String CALL_FUNC = "CALL_FUNC";
	public static final String CALL_FUNC_PTR = "CALL_FUNC_PTR";

	// public static AIdentifierVarExpIR createIdentifier(String name,
	// org.overture.ast.node.INode derrivedFrom)
	// {
	// AIdentifierVarExpIR ident = new AIdentifierVarExpIR();
	// ident.setName(name);
	// ident.setIsLocal(true);
	// ident.setSourceNode(new SourceNode(derrivedFrom));
	// return ident;
	// }

	public static AIdentifierPatternIR newIdentifierPattern(String name)
	{
		AIdentifierPatternIR id = new AIdentifierPatternIR();
		id.setName(name);
		return id;
	}

	public static AIdentifierVarExpIR newIdentifier(String name,
			SourceNode derrivedFrom)
	{
		return createIdentifier(name, derrivedFrom);
	}

	public static AIdentifierVarExpIR createIdentifier(String name,
			SourceNode derrivedFrom)
	{
		AIdentifierVarExpIR ident = new AIdentifierVarExpIR();
		ident.setName(name);
		ident.setIsLocal(true);
		ident.setSourceNode(derrivedFrom);
		return ident;
	}

	public static AVarDeclIR newDeclarationAssignment(SPatternIR varName,
			STypeIR varType, SExpIR value, SourceNode derrivedFrom)
	{
		AVarDeclIR retVar = new AVarDeclIR();
		retVar.setType(varType);
		retVar.setPattern(varName);
		retVar.setSourceNode(derrivedFrom);
		retVar.setExp(value);
		return retVar;
	}

	public static AVarDeclIR newDeclarationAssignment(String varName,
			STypeIR varType, SExpIR value, SourceNode derrivedFrom)
	{
		AIdentifierPatternIR id = new AIdentifierPatternIR();
		id.setName(varName);
		return newDeclarationAssignment(id, varType, value, derrivedFrom);
	}

	public static AAssignToExpStmIR newAssignment(SExpIR to, SExpIR from)
	{
		AAssignToExpStmIR assign = new AAssignToExpStmIR();
		assign.setTarget(to);
		assign.setExp(from);
		return assign;
	}

	public static String getMethodId(AMethodDeclIR selectedMethod, String methodOwnerType) {
		return String.format(CTransUtil.METHOD_CALL_ID_PATTERN, methodOwnerType, selectedMethod.getName());
	}

	@SuppressWarnings("deprecation")
	public static SExpIR newCast(String string, SExpIR newApply)
	{
		ACastUnaryExpIR cast = new ACastUnaryExpIR();
		cast.setExp(newApply);
		cast.setType(new AExternalTypeIR(null, null, null, false, null, string, null));
		return cast;
	}

	public static SStmIR newReturnStm(SExpIR createIdentifier)
	{
		AReturnStmIR ret = new AReturnStmIR();
		ret.setExp(createIdentifier);
		return ret;
	}

	public static STypeIR newTvpType()
	{
		return newExternalType("TVP");
	}

	@SuppressWarnings("deprecation")
	public static STypeIR newExternalType(String name)
	{
		return new AExternalTypeIR(false, null, name, null);
	}

	public static AApplyExpIR newApply(String name, SExpIR... args)
	{
		AApplyExpIR apply = new AApplyExpIR();
		apply.setRoot(createIdentifier(name, null));
		if (args != null)
		{
			apply.setArgs(Arrays.asList(args));
		}
		return apply;
	}

	public static AMacroApplyExpIR newMacroApply(String name, SExpIR... args)
	{
		AMacroApplyExpIR apply = new AMacroApplyExpIR();
    // TODO: Use more accurate type
		apply.setType(new AUnknownTypeIR());
		apply.setRoot(createIdentifier(name, null));
		if (args != null)
		{
			apply.setArgs(Arrays.asList(args));
		}
		return apply;
	}

	public static SStmIR exp2Stm(SExpIR exp)
	{
		AExpStmIR stm = new AExpStmIR();
		stm.setExp(exp);
		return stm;
	}

	public static SStmIR toStm(SExpIR exp)
	{
		return exp2Stm(exp);
	}

	public static SExpIR toExp(SStmIR stm)
	{
		AStmExpIR exp = new AStmExpIR();
		exp.setStm(stm);
		return exp;
	}

	public static void addArgument(String name, STypeIR type, int index,
			List<AFormalParamLocalParamIR> formals)
	{
		LinkedList<AFormalParamLocalParamIR> f = new LinkedList<>();

		AFormalParamLocalParamIR cl = new AFormalParamLocalParamIR();
		AIdentifierPatternIR id = new AIdentifierPatternIR();

		// AIntNumericBasicTypeIR ty = new AIntNumericBasicTypeIR();

		// Create the special new parameter for each operation
		// cl.setTag("class");
		id.setName(name); // This one gets printed
		cl.setPattern(id);
		cl.setType(type);

		f.add(cl);

		// add as first argument
		formals.add(index, cl);
	}

	/**
	 * encapsulate expressions written in c which should not be further transformed
	 * 
	 * @param exp
	 * @return
	 */
	public static SExpIR newCExp(SExpIR exp)
	{
		ACExpIR wrapper = new ACExpIR();
		wrapper.setExp(exp);
		return wrapper;
	}

	public static SExpIR newPtrDeref(SExpIR root, SExpIR target)
	{
		APtrDerefExpIR deref = new APtrDerefExpIR();
		deref.setRoot(root);
		deref.setTarget(target);
		return deref;
	}

	public static SExpIR newArrayIndex(SExpIR array, SExpIR index)
	{
		AArrayIndexExpIR exp = new AArrayIndexExpIR();
		exp.setRoot(array);
		exp.setIndex(index);
		return exp;
	}

	public static AApplyExpIR rewriteToApply(IApplyAssistant assist,
			SExpIR node, String string, SExpIR... args)
			throws AnalysisException
	{
		AApplyExpIR apply = newApply(string);
		apply.setSourceNode(node.getSourceNode());
		if (node.getType() != null)
		{
			apply.setType(node.getType().clone());
		}
		assist.getAssist().replaceNodeWith(node, apply);
		for (SExpIR arg : args)
		{
			apply.getArgs().add(arg);
			if (arg != node)
			{
				arg.apply(assist);
			}
		}
		apply.setSourceNode(node.getSourceNode());
		return apply;
	}

	public static SExpIR newIntLiteralExp(long i)
	{
		AIntLiteralExpIR exp = new AIntLiteralExpIR();
		exp.setValue(i);
		return exp;
	}

	public static SExpIR newParen(SExpIR exp)
	{
		AParenExpIR parent = new AParenExpIR();
		parent.setExp(exp);
		return parent;
	}

	public static SStmIR newLocalDefinition(AVarDeclIR decl)
	{
		ALocalVariableDeclarationStmIR localDef = new ALocalVariableDeclarationStmIR();
		localDef.setDecleration(decl);
		localDef.setSourceNode(decl.getSourceNode());
		return localDef;
	}

	public static AMethodDeclIR newMethod(String name, SStmIR body,
			STypeIR returnType, boolean mangleName) throws AnalysisException
	{
		AMethodDeclIR method = new AMethodDeclIR();
		method.setAbstract(false);
		method.setAsync(false);
		method.setImplicit(false);
		method.setStatic(true);
		method.setIsConstructor(false);
		method.setBody(body);
		AMethodTypeIR mtype = new AMethodTypeIR();
		mtype.setResult(returnType);
		method.setMethodType(mtype);
		method.setName(name);
		if (mangleName)
		{
			method.setName(NameMangler.mangle(method));
		}
		return method;
	}

	public static AMethodDeclIR newInternalMethod(String name, SStmIR body,
			STypeIR returnType, boolean mangleName) throws AnalysisException
	{
		AMethodDeclIR method = newMethod(name, body, returnType, mangleName);
		method.setTag(new Vdm2cTag().addMethodTag(MethodTag.Internal));
		return method;
	}

	public static boolean isValueDefinition(AFieldDeclIR node)
	{

		if (node.getSourceNode() != null
				&& node.getSourceNode().getVdmNode() != null)
		{
			INode vdmNode = node.getSourceNode().getVdmNode();
			return vdmNode instanceof AValueDefinition
					|| vdmNode instanceof ALocalDefinition
					&& (((ALocalDefinition) vdmNode).getValueDefinition() != null);
		}
		return false;
	}

	public static PDefinition unwrapInheritedDef(PDefinition def)
	{
		while (def instanceof AInheritedDefinition)
		{
			def = ((AInheritedDefinition) def).getSuperdef();
		}
		return def;
	}

	public static boolean isValueDefinition(AExplicitVarExpIR node)
	{
		INode vdmNode = node.getSourceNode().getVdmNode();
		if (vdmNode instanceof AVariableExp)
		{
			AVariableExp varExp = (AVariableExp) vdmNode;

			PDefinition def = unwrapInheritedDef(varExp.getVardef());
			if (def instanceof ALocalDefinition)
			{
				ALocalDefinition local = (ALocalDefinition) def;
				if (local.getValueDefinition() != null)
				{
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isStaticDefinition(AFieldDeclIR node)
	{

		if (node.getSourceNode() != null
				&& node.getSourceNode().getVdmNode() != null)
		{
			INode vdmNode = node.getSourceNode().getVdmNode();
			if (vdmNode instanceof AInstanceVariableDefinition)
			{
				AInstanceVariableDefinition field = (AInstanceVariableDefinition) vdmNode;
				if (field.getAccess().getStatic() != null)
				{
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isStaticFieldDefinition(SVarExpIR node,
			IRInfo info)
	{
		if(node.getSourceNode() == null)
		{
			// It's likely that this node was constructed by one of the transformations. Assume that it does not
			// originate from a static field declaration
			return false;
		}
		
		INode vdmNode = node.getSourceNode().getVdmNode();

		PDefinition def = null;

		if (vdmNode instanceof AVariableExp)
		{
			AVariableExp varExp = (AVariableExp) vdmNode;
			def = varExp.getVardef();
		} else if (vdmNode instanceof AIdentifierStateDesignator)
		{
			AIdentifierStateDesignator id = (AIdentifierStateDesignator) vdmNode;
			def = info.getIdStateDesignatorDefs().get(id);
		}

		if (def != null)
		{
			if (def instanceof AInheritedDefinition)
			{
				def = ((AInheritedDefinition) def).getSuperdef();
			}

			if (def instanceof AInstanceVariableDefinition)
			{
				AInstanceVariableDefinition field = (AInstanceVariableDefinition) def;
				if (field.getAccess().getStatic() != null)
				{
					return true;
				}
			}
		}

		return false;
	}

	public static SClassDeclIR getClass(TransAssistantIR assist, String name)
	{
		for (SClassDeclIR c : assist.getInfo().getClasses())
		{
			if (c.getName().equals(name))
			{
				return c;
			}
		}
		return null;
	}
	
	public static AExternalExpIR consCInt(String cIntStr) {
		AExternalTypeIR cIntType = new AExternalTypeIR();
		cIntType.setName("int");

		AExternalExpIR cIntExp = new AExternalExpIR();
		cIntExp.setType(cIntType);
		cIntExp.setTargetLangExp(cIntStr);

		return cIntExp;
	}
}
