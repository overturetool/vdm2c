package org.overture.codegen.vdm2c.distribution.transformations;

import java.util.LinkedList;
import java.util.Set;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.AIntLiteralExpIR;
import org.overture.codegen.ir.expressions.AOrBoolBinaryExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.AIfStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.ANatNumericBasicTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.distribution.DistCGenUtil;
import org.overture.codegen.vdm2c.distribution.SystemArchitectureAnalysis;

public class GenerateClassDispatchTrans extends DepthFirstAnalysisCAdaptor
{

	public GenerateClassDispatchTrans(TransAssistantIR transformationAssistant)
	{
	}

	@Override
	public void caseADefaultClassDeclIR(ADefaultClassDeclIR node)
			throws AnalysisException
	{
		ADefaultClassDeclIR cl = node;

		if (node.getTag() != null)
		{
			for (String clName : SystemArchitectureAnalysis.systemClasses)
			{
				// Create a new method:
				AMethodDeclIR m = new AMethodDeclIR();
				m.setAsync(false);
				m.setAbstract(false);
				m.setAccess("public");
				m.setImplicit(false);
				m.setIsConstructor(false);
				m.setStatic(false);
				m.setName("dist" + clName);

				LinkedList<AFormalParamLocalParamIR> par = new LinkedList<AFormalParamLocalParamIR>();

				// First parameter
				AFormalParamLocalParamIR par1 = DistCGenUtil.addMethodParameter("obj", "TVP");
				par.add(par1);

				// Second parameter
				AFormalParamLocalParamIR par2 = DistCGenUtil.addMethodParameter("funID", "int");
				par.add(par2);

				// Third parameter
				AFormalParamLocalParamIR par3 = DistCGenUtil.addMethodParameter("nrArgs", "int");
				par.add(par3);

				// Fourth parameter
				AFormalParamLocalParamIR par4 = DistCGenUtil.addMethodParameter("args []", "TVP");
				par.add(par4);

				// The method return type
				AExternalTypeIR tyRet = new AExternalTypeIR();
				tyRet.setName("TVP");

				AMethodTypeIR mTy = new AMethodTypeIR();

				LinkedList<STypeIR> paramsTy = new LinkedList<STypeIR>();
				paramsTy.add(new AIntNumericBasicTypeIR());
				paramsTy.add(new AIntNumericBasicTypeIR());

				mTy.setParams(paramsTy);
				mTy.setResult(tyRet);

				m.setMethodType(mTy.clone());
				m.setFormalParams(par);

				// New method body
				ABlockStmIR body = new ABlockStmIR();
				body.setScoped(false);

				// Create the statements
				LinkedList<SStmIR> st = new LinkedList<SStmIR>();

				int i;
				for (i = 0; i < 4; i++)
				{
					// Create the if statements
					AIfStmIR first = new AIfStmIR();

					// Exp part
					AEqualsBinaryExpIR bin = new AEqualsBinaryExpIR();

					// Left side , Always the same
					AIdentifierVarExpIR id = DistCGenUtil.createIdExpIntTyp("nrArgs");
					bin.setLeft(id);

					// Right side: Add id to if for this specific bus
					// Add 1 since we start from 1 and not 0

					AIntLiteralExpIR val = new AIntLiteralExpIR();
					val.setType(new ANatNumericBasicTypeIR());
					val.setValue((long) i);
					bin.setRight(val);

					first.setIfExp(bin);

					// Then part, e.g. call the specific BUS
					AReturnStmIR ret = new AReturnStmIR();
					AApplyExpIR app = new AApplyExpIR();

					LinkedList<SExpIR> args = new LinkedList<SExpIR>();

					// 1. argument: Global variable name
					AIdentifierVarExpIR arg1 = DistCGenUtil.createIdExpTVPTyp(clName);
					args.add(arg1);
					args.add(arg1.clone());

					AIdentifierVarExpIR arg2 = DistCGenUtil.createIdExpTVPTyp("obj");
					args.add(arg2);

					// 2. argument: function id
					AIdentifierVarExpIR arg3 = DistCGenUtil.createIdExpIntTyp("funID");
					args.add(arg3);

					// 4. argument: Array of arguments
					int b = 0;
					for (b = 0; b < i; b++)
					{
						AIdentifierVarExpIR arg4 = DistCGenUtil.createIdExpIntTyp("args["
								+ b + "]");
						args.add(arg4);
					}
					// args.add(id);
					app.setArgs(args);

					// Type
					app.setType(new AIntNumericBasicTypeIR());

					// Root
					AIdentifierVarExpIR idVar = new AIdentifierVarExpIR();
					idVar.setIsLambda(false);
					idVar.setIsLocal(false);
					idVar.setName("CALL_FUNC");
					
					// set method type
					idVar.setType(mTy.clone());
					app.setRoot(idVar);
					ret.setExp(app);
					first.setThenStm(ret);
					st.add(first);
				}
				body.setStatements(st);
				m.setBody(body);

				cl.getMethods().add(m);
			}
		}
	}
}
