package org.overture.codegen.vdm2c.utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.ACastUnaryExpIR;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.AIntLiteralExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.AExpStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.SourceNode;
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
		apply.setType(node.getType());
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

	public static AMethodDeclIR newInternalMethod(String name, SStmIR body,
			STypeIR returnType) throws AnalysisException
	{
		AMethodDeclIR method = new AMethodDeclIR();
		method.setAbstract(false);
		method.setAsync(false);
		method.setImplicit(false);
		method.setStatic(true);
		method.setIsConstructor(false);
		method.setTag(new Vdm2cTag().addMethodTag(MethodTag.Internal));
		method.setBody(body);
		AMethodTypeIR mtype = new AMethodTypeIR();
		mtype.setResult(returnType);
		method.setMethodType(mtype);
		method.setName(name);
		method.setName(NameMangler.mangle(method));
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
					&& ((ALocalDefinition) vdmNode).getValueDefinition();
		}
		return false;
	}

	public static boolean isValueDefinition(AExplicitVarExpIR node)
	{
		INode vdmNode = node.getSourceNode().getVdmNode();
		if (vdmNode instanceof AVariableExp)
		{
			AVariableExp varExp = (AVariableExp) vdmNode;
			if (varExp.getVardef() instanceof ALocalDefinition)
			{
				ALocalDefinition local = (ALocalDefinition) varExp.getVardef();
				if (local.getValueDefinition())
				{
					return true;
				}
			}
		}
		return false;
	}

}
