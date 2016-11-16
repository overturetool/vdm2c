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

		//		System.out.println("Dist transformation, method name: " + node.getName());

		if(node.getTag()!=null){

			String cpu = cl.getTag().toString();


			for(String clName : SystemArchitectureAnalysis.systemClasses){

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
				AFormalParamLocalParamIR par1 = new AFormalParamLocalParamIR();
				AIdentifierPatternIR idPat1 = new AIdentifierPatternIR();
				idPat1.setName("obj");
				par1.setPattern(idPat1);
				AExternalTypeIR tyPat = new AExternalTypeIR();
				tyPat.setName("TVP");
				par1.setType(tyPat);
				par.add(par1);

				AExternalTypeIR tyInt = new AExternalTypeIR();
				tyInt.setName("int");

				// Second parameter
				AFormalParamLocalParamIR par2 = new AFormalParamLocalParamIR();
				AIdentifierPatternIR idPat2 = new AIdentifierPatternIR();
				idPat2.setName("funID");
				par2.setPattern(idPat2);
				par2.setType(tyInt.clone());
				par.add(par2);

				// Third parameter
				AFormalParamLocalParamIR par4 = new AFormalParamLocalParamIR();
				AIdentifierPatternIR idPat4 = new AIdentifierPatternIR();
				idPat4.setName("nrArgs");
				par4.setPattern(idPat4);
				par4.setType(tyInt.clone());
				par.add(par4);

				// Fourth parameter
				AFormalParamLocalParamIR par3 = new AFormalParamLocalParamIR();
				AIdentifierPatternIR idPat3 = new AIdentifierPatternIR();
				idPat3.setName("args []");
				par3.setPattern(idPat3);
				AExternalTypeIR tyVar = new AExternalTypeIR();
				tyVar.setName("TVP");
				par3.setType(tyVar);
				par.add(par3);

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

				int maxArgs = 4;

				int i;
				for(i=0; i<4; i++){

					// create the if statements
					AIfStmIR first = new AIfStmIR();

					//				//**** Exp part
					AEqualsBinaryExpIR bin = new AEqualsBinaryExpIR();

					// Left side , Always the same
					AIdentifierVarExpIR id = new AIdentifierVarExpIR();
					id.setIsLambda(false);
					id.setIsLocal(true);
					id.setName("nrArgs");
					id.setType(new AIntNumericBasicTypeIR());

					bin.setLeft(id);

					// Right side: Add id to if for this specific bus
					// Add 1 since we start from 1 and not 0

					AIntLiteralExpIR val = new AIntLiteralExpIR();
					val.setType(new ANatNumericBasicTypeIR());
					val.setValue((long) i);
					bin.setRight(val);

					first.setIfExp(bin);

					//**** Then part, e.g. call the specific BUS
					AReturnStmIR ret = new AReturnStmIR();
					AApplyExpIR app = new AApplyExpIR();
					// Set args
					LinkedList<SExpIR> args = new LinkedList<SExpIR>();
					//				AIntLiteralExpIR v = new AIntLiteralExpIR();
					////				v.setValue((long) 6);

					// 1. argument: Global variable name
					AIdentifierVarExpIR arg0 = new AIdentifierVarExpIR();
					arg0.setIsLambda(false);
					arg0.setIsLocal(true);
					arg0.setName(clName);
					arg0.setType(tyPat.clone());
					args.add(arg0);
					args.add(arg0.clone());

					AIdentifierVarExpIR arg1 = new AIdentifierVarExpIR();
					arg1.setIsLambda(false);
					arg1.setIsLocal(true);
					arg1.setName("obj");
					arg1.setType(tyPat.clone());
					args.add(arg1);

					// 2. argument: function id
					AIdentifierVarExpIR arg2 = new AIdentifierVarExpIR();
					arg2.setIsLambda(false);
					arg2.setIsLocal(true);
					arg2.setName("funID");
					arg2.setType(new AIntNumericBasicTypeIR());
					args.add(arg2);

					// 4. argument: Array of arguments

					int b=0;
					for(b=0; b<i; b++){
						AIdentifierVarExpIR arg4 = new AIdentifierVarExpIR();
						arg4.setIsLambda(false);
						arg4.setIsLocal(true);
						arg4.setName("args[" + b + "]");
						arg4.setType(new AIntNumericBasicTypeIR());
						args.add(arg4);
					}
					//args.add(id);
					app.setArgs(args);


					// Type
					app.setType(new AIntNumericBasicTypeIR());

					// Root
					AIdentifierVarExpIR idVar = new AIdentifierVarExpIR();
					idVar.setIsLambda(false);
					idVar.setIsLocal(false);

					// get id of current object:
					//				String name = "Zone";
					//
					idVar.setName("CALL_FUNC");
					//				// set method type
					idVar.setType(mTy.clone());
					app.setRoot(idVar);
					ret.setExp(app);
					//
					first.setThenStm(ret);
					//
					//first.setIfExp(orBin);
					//
					st.add(first);
					//st.add(first.clone());
				}
				body.setStatements(st);
				m.setBody(body);

				cl.getMethods().add(m);
			}





			/** One if statement pr. bus for a cpu ***/

			//			for(String bus : SystemArchitectureAnalysis.connectionMapStr.keySet()){
			//
			//				// 1. statement
			//				//				AIfStmIR first = new AIfStmIR();
			//
			//				//**** Exp part
			//				//AEqualsBinaryExpIR bin = new AEqualsBinaryExpIR();
			//
			//				// Left side , Always the same
			//				AIdentifierVarExpIR id = new AIdentifierVarExpIR();
			//				id.setIsLambda(false);
			//				id.setIsLocal(true);
			//				id.setName("objID");
			//				id.setType(new AIntNumericBasicTypeIR());
			//
			//
			//				LinkedList<AEqualsBinaryExpIR> binList = new LinkedList<AEqualsBinaryExpIR>();
			//
			//				Set<String> cpuList = SystemArchitectureAnalysis.connectionMapStr.get(bus);
			//
			//				int obj_id = 0;
			//
			//				if(cpuList.contains(cpu)){
			//					for(String c : cpuList){
			//						if(c.equals(cpu)){
			//
			//							// get deployed objects of the current CPU
			//							Set<String> depObj = SystemArchitectureAnalysis.distributionMapStr.get(c);
			//
			//							for(String dobj : depObj){
			//								// One if statement pr. deployed object
			//
			//								int idVal = SystemArchitectureAnalysis.systemDeployedObjectsStr.indexOf(dobj) + 1;
			//								LinkedList<AFieldDeclIR> va = SystemArchitectureAnalysis.systemDeployedObjects;
			//								obj_id = idVal - 1; // stor current object id
			//								AFieldDeclIR o = va.get(obj_id);
			//
			//								AIfStmIR first = new AIfStmIR();
			//
			//								AEqualsBinaryExpIR binEq = new AEqualsBinaryExpIR();
			//								// Left side: id
			//								AIdentifierVarExpIR idObj = new AIdentifierVarExpIR();
			//								idObj.setIsLambda(false);
			//								idObj.setIsLocal(true);
			//								idObj.setName("objID");
			//								idObj.setType(new AIntNumericBasicTypeIR());
			//
			//								binEq.setLeft(idObj);
			//								// Right side: Add id to if for this specific bus
			//								// Add 1 since we start from 1 and not 0
			//
			//								AIntLiteralExpIR val = new AIntLiteralExpIR();
			//								val.setType(new ANatNumericBasicTypeIR());
			//								val.setValue((long) idVal);
			//								binEq.setRight(val);
			//
			//								first.setIfExp(binEq);
			//
			//								//binList.add(binEq);
			//
			//
			//
			//
			//								//**** Then part, e.g. call the specific BUS
			//								AReturnStmIR ret = new AReturnStmIR();
			//								AApplyExpIR app = new AApplyExpIR();
			//								// Set args
			//								LinkedList<SExpIR> args = new LinkedList<SExpIR>();
			//								AIntLiteralExpIR v = new AIntLiteralExpIR();
			//								v.setValue((long) 6);
			//
			//								// 1. argument: Global variable name
			//								AIdentifierVarExpIR arg1 = new AIdentifierVarExpIR();
			//								arg1.setIsLambda(false);
			//								arg1.setIsLocal(true);
			//								arg1.setName("g_" + SystemArchitectureAnalysis.systemName + "_" + o.getName());
			//								arg1.setType(new AIntNumericBasicTypeIR());
			//								args.add(arg1);
			//
			//								// 2. argument: function id
			//								AIdentifierVarExpIR arg2 = new AIdentifierVarExpIR();
			//								arg2.setIsLambda(false);
			//								arg2.setIsLocal(true);
			//								arg2.setName("funID");
			//								arg2.setType(new AIntNumericBasicTypeIR());
			//								args.add(arg2);
			//
			//								// 3. argument: number of arguments
			//								AIdentifierVarExpIR arg3 = new AIdentifierVarExpIR();
			//								arg3.setIsLambda(false);
			//								arg3.setIsLocal(true);
			//								arg3.setName("nrArgs");
			//								arg3.setType(new AIntNumericBasicTypeIR());
			//								args.add(arg3);
			//
			//								// 4. argument: Array of arguments
			//								AIdentifierVarExpIR arg4 = new AIdentifierVarExpIR();
			//								arg4.setIsLambda(false);
			//								arg4.setIsLocal(true);
			//								arg4.setName("args");
			//								arg4.setType(new AIntNumericBasicTypeIR());
			//								args.add(arg4);
			//
			//								//args.add(id);
			//								app.setArgs(args);
			//
			//
			//								// Type
			//								app.setType(new AIntNumericBasicTypeIR());
			//
			//								// Root
			//								AIdentifierVarExpIR idVar = new AIdentifierVarExpIR();
			//								idVar.setIsLambda(false);
			//								idVar.setIsLocal(false);
			//
			//								// get id of current object:
			//								String name = "Zone";
			//
			//								idVar.setName("dist" + o.getType().toString());
			//								// set method type
			//								idVar.setType(mTy.clone());
			//								app.setRoot(idVar);
			//								ret.setExp(app);
			//
			//								first.setThenStm(ret);
			//
			//								//first.setIfExp(orBin);
			//
			//								st.add(first);
			//								//st.add(first.clone());
			//
			//							}
			//						}
			//					}
			//				}
			//
			//				body.setStatements(st);

		}
		//LinkedList<SStmIR> s = ((ABlockStmIR) node.getBody()).getStatements();

		//m.setBody(body);
		//			cl.getMethods().add(m);
		//System.out.println();

	}



}
