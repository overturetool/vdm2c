package org.overture.codegen.vdm2c.distribution.transformations;

import java.util.LinkedList;
import java.util.Set;

import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AOptionalType;
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

public class GenerateGetResTrans extends DepthFirstAnalysisCAdaptor
{

	public GenerateGetResTrans(TransAssistantIR transformationAssistant)
	{
	}

	@Override
	public void caseADefaultClassDeclIR(ADefaultClassDeclIR node)
			throws AnalysisException
	{
		ADefaultClassDeclIR cl = node;

		if (node.getTag() != null)
		{

			String cpu = cl.getTag().toString();

			// Create a new method:
			AMethodDeclIR m = new AMethodDeclIR();
			m.setAsync(false);
			m.setAbstract(false);
			m.setAccess("public");
			m.setImplicit(false);
			m.setIsConstructor(false);
			m.setStatic(false);
			m.setName("getRes");

			LinkedList<AFormalParamLocalParamIR> par = new LinkedList<AFormalParamLocalParamIR>();

			// First parameter
			AFormalParamLocalParamIR par1 = DistCGenUtil.addMethodParameter("objID", "int");
			par.add(par1);

			// Second parameter
			AFormalParamLocalParamIR par2 = DistCGenUtil.addMethodParameter("funID", "int");
			par.add(par2);

			// Third parameter
			AFormalParamLocalParamIR par3 = DistCGenUtil.addMethodParameter("supID", "int");
			par.add(par3);

			// Fourth parameter
			AFormalParamLocalParamIR par4 = DistCGenUtil.addMethodParameter("nrArgs", "int");
			par.add(par4);

			// Fifth parameter
			AFormalParamLocalParamIR par5 = DistCGenUtil.addMethodParameter("args []", "TVP");
			par.add(par5);

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

			/** One if statement pr. bus for a cpu ***/

			for (String bus : SystemArchitectureAnalysis.connectionMapStr.keySet())
			{
				Set<String> cpuList = SystemArchitectureAnalysis.connectionMapStr.get(bus);

				int obj_id = 0;

				if (cpuList.contains(cpu))
				{
					for (String c : cpuList)
					{
						if (c.equals(cpu))
						{

							// get deployed objects of the current CPU
							Set<String> depObj = SystemArchitectureAnalysis.distributionMapStr.get(c);

							for (String dobj : depObj)
							{
								// One if statement pr. deployed object
								int idVal = SystemArchitectureAnalysis.systemDeployedObjectsStr.indexOf(dobj);
										//+ 1;
								LinkedList<AFieldDeclIR> va = SystemArchitectureAnalysis.systemDeployedObjects;
								obj_id = idVal; //- 1; // stor current object id
								AFieldDeclIR o = va.get(obj_id);

								LinkedList<ILexNameToken> classSupNames = new LinkedList<ILexNameToken>();

								if (o.getType().getSourceNode().getVdmNode() instanceof AOptionalType)
								{
									AOptionalType classNN = (AOptionalType) o.getType().getSourceNode().getVdmNode();

									AClassType classN = (AClassType) classNN.getType();
									
									classSupNames = classN.getClassdef().getSupernames();
								}

								LinkedList<String> classNames = new LinkedList<String>();

								classNames.add(o.getType().toString());

								for (ILexNameToken sup : classSupNames)
								{
									classNames.add(sup.toString());
								}

								AIfStmIR firstStm = new AIfStmIR();

								AEqualsBinaryExpIR binEqStm = new AEqualsBinaryExpIR();

								// Left side: id
								AIdentifierVarExpIR idObjStm = DistCGenUtil.createIdExpIntTyp("objID");
								binEqStm.setLeft(idObjStm);

								// Right side: Add id to if for this specific bus
								// Add 1 since we start from 1 and not 0
								AIntLiteralExpIR val = new AIntLiteralExpIR();
								val.setType(new ANatNumericBasicTypeIR());
								val.setValue((long) idVal);
								binEqStm.setRight(val);

								firstStm.setIfExp(binEqStm);

								int len = classSupNames.size() + 1;

								ABlockStmIR if_block = new ABlockStmIR();

								for (int p = 0; p < len; p++)
								{

									AIfStmIR first = new AIfStmIR();

									AEqualsBinaryExpIR binEq = new AEqualsBinaryExpIR();

									// Left side: id
									AIdentifierVarExpIR idObj = DistCGenUtil.createIdExpIntTyp("supID");
									binEq.setLeft(idObj);

									// Right side: Add id to if for this specific bus
									// Add 1 since we start from 1 and not 0
									AIdentifierVarExpIR valS = DistCGenUtil.createIdExpIntTyp("CLASS_ID_"
											+ classNames.get(p) + "_ID");
									binEq.setRight(valS);

									first.setIfExp(binEq);

									// **** Then part, e.g. call the specific BUS
									// Then part is a block statement pr. inherited class

									AReturnStmIR ret = new AReturnStmIR();
									AApplyExpIR app = new AApplyExpIR();

									// Set args
									LinkedList<SExpIR> args = new LinkedList<SExpIR>();
									// AIntLiteralExpIR v = new AIntLiteralExpIR();
									// v.setValue((long) 6);

									// 1. argument: Global variable name
									AIdentifierVarExpIR arg1 = DistCGenUtil.createIdExpIntTyp("g_"
											+ SystemArchitectureAnalysis.systemName
											+ "_" + o.getName());
									args.add(arg1);

									// 2. argument: function id
									AIdentifierVarExpIR arg2 = DistCGenUtil.createIdExpIntTyp("funID");
									args.add(arg2);

									// 3. argument: number of arguments
									AIdentifierVarExpIR arg3 = DistCGenUtil.createIdExpIntTyp("nrArgs");
									args.add(arg3);

									// 4. argument: Array of arguments
									AIdentifierVarExpIR arg4 = DistCGenUtil.createIdExpIntTyp("args");
									args.add(arg4);

									app.setArgs(args);

									// Type
									app.setType(new AIntNumericBasicTypeIR());

									// Root
									AIdentifierVarExpIR idVar = new AIdentifierVarExpIR();
									idVar.setIsLambda(false);
									idVar.setIsLocal(false);

									idVar.setName("dist" + classNames.get(p));
									// set method type
									idVar.setType(mTy.clone());
									app.setRoot(idVar);
									ret.setExp(app);

									// Create inner if statement for
									first.setThenStm(ret);
									if_block.getStatements().add(first.clone());
								}
								firstStm.setThenStm(if_block);
								st.add(firstStm);
							}
						}
					}
				}
				body.setStatements(st);
			}
			m.setBody(body);
			cl.getMethods().add(m);
		}
	}
}
