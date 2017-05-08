package org.overture.codegen.vdm2c.distribution.transformations;

import java.util.LinkedList;
import java.util.Set;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.AIntLiteralExpIR;
import org.overture.codegen.ir.expressions.AOrBoolBinaryExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.AExpStmIR;
import org.overture.codegen.ir.statements.AIfStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.ANatNumericBasicTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.distribution.DistCGenUtil;
import org.overture.codegen.vdm2c.distribution.SystemArchitectureAnalysis;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;
import org.overture.codegen.vdm2c.extast.statements.ALocalVariableDeclarationStmIR;

public class GenerateSendBusTrans extends DepthFirstAnalysisCAdaptor
{

	public GenerateSendBusTrans(TransAssistantIR transformationAssistant)
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

			// Create a new method: 
			AMethodDeclIR m = new AMethodDeclIR();

			m.setAsync(false);
			m.setAbstract(false);
			m.setAccess("public");
			m.setImplicit(false);
			m.setIsConstructor(false);
			m.setStatic(false);
			m.setName("send_bus");

			LinkedList<AFormalParamLocalParamIR> par = new LinkedList<AFormalParamLocalParamIR>();

			// Add method arguments
			AFormalParamLocalParamIR par1 = DistCGenUtil.addMethodParameter("objID", "int");
			par.add(par1);

			AFormalParamLocalParamIR par2 = DistCGenUtil.addMethodParameter("funID", "int");
			par.add(par2);
			
			AFormalParamLocalParamIR par3 = DistCGenUtil.addMethodParameter("supID", "int");
			par.add(par3);
			
			AFormalParamLocalParamIR par4 = DistCGenUtil.addMethodParameter("nrArgs", "int");
			par.add(par4);

			AFormalParamLocalParamIR par5 = DistCGenUtil.addMethodParameter("", "...");
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

			for(String bus : SystemArchitectureAnalysis.connectionMapStr.keySet()){

				// 1. statement
				AIfStmIR first = new AIfStmIR();

				//**** Exp part
				//AEqualsBinaryExpIR bin = new AEqualsBinaryExpIR();

				AIdentifierVarExpIR id = DistCGenUtil.createIdExpIntTyp("objID");

				LinkedList<AEqualsBinaryExpIR> binList = new LinkedList<AEqualsBinaryExpIR>();
				Set<String> cpuList = SystemArchitectureAnalysis.connectionMapStr.get(bus);

				if(cpuList.contains(cpu)){
					for(String c : cpuList){
						if(c.equals(cpu)) continue;

						// get deployed objects of a given cpu
						Set<String> depObj = SystemArchitectureAnalysis.distributionMapStr.get(c);
						for(String dobj : depObj){
							AEqualsBinaryExpIR binEq = new AEqualsBinaryExpIR();
							// Left side: id
							binEq.setLeft(id.clone());
							// Right side: Add id to if for this specific bus
							// Add 1 since we start from 1 and not 0
							int idVal = SystemArchitectureAnalysis.systemDeployedObjectsStr.indexOf(dobj) + 1;
							AIntLiteralExpIR val = new AIntLiteralExpIR();
							val.setType(new ANatNumericBasicTypeIR());
							val.setValue((long) idVal);
							binEq.setRight(val);
							binList.add(binEq);
						}
					}
				}

				// TODO: If no bus exists we get an error
				if(binList.size()==1) first.setIfExp(binList.get(0));
				else{
					LinkedList<AOrBoolBinaryExpIR> orBinList = new LinkedList<AOrBoolBinaryExpIR>(); 
					for(AEqualsBinaryExpIR li : binList){
						AOrBoolBinaryExpIR orb = new AOrBoolBinaryExpIR();
						orBinList.add(orb);
					}
					
					if(orBinList.isEmpty())
						continue;
						
					orBinList.remove();

					int i = 0;
					for(AOrBoolBinaryExpIR ors : orBinList){
						if(i==orBinList.size()-1){
							ors.setLeft(binList.get(i));
							ors.setRight(binList.get(i+1));
						}
						else{
							ors.setLeft(binList.get(i));
							ors.setRight(orBinList.get(i+1));
							i = i + 1;
						}
					}

					first.setIfExp(orBinList.get(0));
				}

				// Then part, e.g. call the specific BUS
				AReturnStmIR ret = new AReturnStmIR();
				AApplyExpIR app = new AApplyExpIR();

				// Set arguments
				LinkedList<SExpIR> args = new LinkedList<SExpIR>();

				// 1. argument
				args.add(id); 

				// 2. argument
				AIdentifierVarExpIR funID = DistCGenUtil.createIdExpIntTyp("funID");
				args.add(funID);

				// 3. argument
				AIdentifierVarExpIR supID = DistCGenUtil.createIdExpIntTyp("supID");
				args.add(supID);

				// 4. argument
				AIdentifierVarExpIR nrArgs = DistCGenUtil.createIdExpIntTyp("nrArgs");
				args.add(nrArgs);

				// 5. argument
				AIdentifierVarExpIR ar = DistCGenUtil.createIdExpIntTyp("args");
				args.add(ar);

				// Set arguments of apply expression
				app.setArgs(args);

				// Type
				app.setType(new AIntNumericBasicTypeIR());

				// Root
				AIdentifierVarExpIR idVar = new AIdentifierVarExpIR();
				idVar.setIsLambda(false);
				idVar.setIsLocal(false);
				idVar.setName(bus);
				// set method type
				idVar.setType(mTy.clone());
				app.setRoot(idVar);
				ret.setExp(app);

				first.setThenStm(ret);
				st.add(first);
			}

			body.setStatements(st);

			AVarDeclIR args_list = new AVarDeclIR();

			AExternalTypeIR tyArgs = new AExternalTypeIR();
			tyArgs.setName("va_list");

			args_list.setType(tyArgs);
			args_list.setExp(null);

			AIdentifierPatternIR idP = new AIdentifierPatternIR();

			idP.setName("args");

			args_list.setPattern(idP);
			args_list.setType(tyArgs.clone());

			ALocalVariableDeclarationStmIR loc = new ALocalVariableDeclarationStmIR();

			loc.setDecleration(args_list);

			body.getStatements().add(0, loc);


			// Add function call
			AExpStmIR expApp = new AExpStmIR();

			AMacroApplyExpIR macApp = new AMacroApplyExpIR();

			// Set arguments
			LinkedList<SExpIR> args = new LinkedList<SExpIR>();

			// 1. argument
			AIdentifierVarExpIR funID = new AIdentifierVarExpIR();
			funID.setIsLambda(false);
			funID.setIsLocal(true);
			funID.setName("args");
			funID.setType(null);
			args.add(funID);

			// 2. argument
			AIdentifierVarExpIR nrArgs = new AIdentifierVarExpIR();
			nrArgs.setIsLambda(false);
			nrArgs.setIsLocal(true);
			nrArgs.setName("nrArgs");
			nrArgs.setType(null);
			args.add(nrArgs);

			// Set arguments
			macApp.setArgs(args);

			// Function call to va_start generation
			AIdentifierVarExpIR va_start = new AIdentifierVarExpIR();
			va_start.setIsLambda(false);
			va_start.setIsLocal(true);
			va_start.setName("va_start");
			va_start.setType(new AIntNumericBasicTypeIR());
			args.add(va_start);

			macApp.setRoot(va_start);

			expApp.setExp(macApp);


			body.getStatements().add(1,expApp);

			m.setBody(body);
			cl.getMethods().add(m);
		}



	}


}
