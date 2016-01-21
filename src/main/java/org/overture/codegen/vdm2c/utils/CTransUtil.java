package org.overture.codegen.vdm2c.utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.AExpStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.vdm2c.extast.expressions.AArrayIndexExpCG;
import org.overture.codegen.vdm2c.extast.expressions.ACExpCG;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpCG;
import org.overture.codegen.vdm2c.extast.expressions.APtrDerefExpCG;

public class CTransUtil
{
	// public static AIdentifierVarExpCG createIdentifier(String name,
	// org.overture.ast.node.INode derrivedFrom)
	// {
	// AIdentifierVarExpCG ident = new AIdentifierVarExpCG();
	// ident.setName(name);
	// ident.setIsLocal(true);
	// ident.setSourceNode(new SourceNode(derrivedFrom));
	// return ident;
	// }

	public static AIdentifierPatternCG newIdentifierPattern(String name)
	{
		AIdentifierPatternCG id = new AIdentifierPatternCG();
		id.setName(name);
		return id;
	}

	public static AIdentifierVarExpCG newIdentifier(String name,
			SourceNode derrivedFrom)
	{
		return createIdentifier(name, derrivedFrom);
	}

	public static AIdentifierVarExpCG createIdentifier(String name,
			SourceNode derrivedFrom)
	{
		AIdentifierVarExpCG ident = new AIdentifierVarExpCG();
		ident.setName(name);
		ident.setIsLocal(true);
		ident.setSourceNode(derrivedFrom);
		return ident;
	}

	public static AVarDeclCG newDeclarationAssignment(String varName,
			STypeCG varType, SExpCG value, SourceNode derrivedFrom)
	{
		AIdentifierPatternCG id = new AIdentifierPatternCG();
		id.setName(varName);

		AVarDeclCG retVar = new AVarDeclCG();
		retVar.setType(varType);
		retVar.setPattern(id);
		retVar.setSourceNode(SourceNode.copy(derrivedFrom));
		retVar.setExp(value);

		AIdentifierVarExpCG retVarOcc = new AIdentifierVarExpCG();
		retVarOcc.setType(retVar.getType().clone());
		retVarOcc.setName(varName);
		retVarOcc.setSourceNode(retVar.getSourceNode());
		retVarOcc.setIsLocal(true);

		return retVar;
	}

	public static AAssignToExpStmCG newAssignment(SExpCG to, SExpCG from)
	{
		AAssignToExpStmCG assign = new AAssignToExpStmCG();
		assign.setTarget(to);
		assign.setExp(from);
		return assign;
	}

	@SuppressWarnings("deprecation")
	public static SExpCG newCast(String string, SExpCG newApply)
	{
		ACastUnaryExpCG cast = new ACastUnaryExpCG();
		cast.setExp(newApply);
		cast.setType(new AExternalTypeCG(null, false, null, string, null));
		return cast;
	}

	public static SStmCG newReturnStm(SExpCG createIdentifier)
	{
		AReturnStmCG ret = new AReturnStmCG();
		ret.setExp(createIdentifier);
		return ret;
	}

	public static STypeCG newTvpType()
	{
		return newExternalType("TVP");
	}

	@SuppressWarnings("deprecation")
	public static STypeCG newExternalType(String name)
	{
		return new AExternalTypeCG(null, false, null, name, null);
	}

	public static AApplyExpCG newApply(String name, SExpCG... args)
	{
		AApplyExpCG apply = new AApplyExpCG();
		apply.setRoot(createIdentifier(name, null));
		if (args != null)
			apply.setArgs(Arrays.asList(args));
		return apply;
	}

	public static AMacroApplyExpCG newMacroApply(String name, SExpCG... args)
	{
		AMacroApplyExpCG apply = new AMacroApplyExpCG();
		apply.setRoot(createIdentifier(name, null));
		if (args != null)
			apply.setArgs(Arrays.asList(args));
		return apply;
	}

	public static SStmCG exp2Stm(SExpCG exp)
	{
		AExpStmCG stm = new AExpStmCG();
		stm.setExp(exp);
		return stm;
	}

	public static SStmCG toStm(SExpCG exp)
	{
		return exp2Stm(exp);
	}

	public static void addArgument(String name, STypeCG type, int index,
			List<AFormalParamLocalParamCG> formals)
	{
		LinkedList<AFormalParamLocalParamCG> f = new LinkedList<>();

		AFormalParamLocalParamCG cl = new AFormalParamLocalParamCG();
		AIdentifierPatternCG id = new AIdentifierPatternCG();

		// AIntNumericBasicTypeCG ty = new AIntNumericBasicTypeCG();

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
	public static SExpCG newCExp(SExpCG exp)
	{
		ACExpCG wrapper = new ACExpCG();
		wrapper.setExp(exp);
		return wrapper;
	}

	public static SExpCG newPtrDeref(SExpCG root, SExpCG target)
	{
		APtrDerefExpCG deref = new APtrDerefExpCG();
		deref.setRoot(root);
		deref.setTarget(target);
		return deref;
	}
	
	public static SExpCG newArrayIndex(SExpCG array, SExpCG index)
	{
		AArrayIndexExpCG exp = new AArrayIndexExpCG();
		exp.setRoot(array);
		exp.setIndex(index);
		return exp;
	}
	
	
	
	public static AApplyExpCG rewriteToApply(IApplyAssistant assist,SExpCG node, String string, SExpCG... args) throws AnalysisException
	{
		AApplyExpCG apply = newApply(string);
		apply.setSourceNode(SourceNode.copy(node.getSourceNode()));
		apply.setType(node.getType());
		assist.getAssist().replaceNodeWith(node, apply);
		for (SExpCG arg : args)
		{
			apply.getArgs().add(arg);
			if(arg!=node)
			arg.apply(assist);
		}
		return apply;
	}
}
