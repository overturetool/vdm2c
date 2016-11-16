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
import org.overture.codegen.ir.statements.AIfStmIR;
import org.overture.codegen.ir.statements.APlainCallStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.ANatNumericBasicTypeIR;
import org.overture.codegen.ir.types.AVoidTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.distribution.SystemArchitectureAnalysis;

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

			// First parameter
			AFormalParamLocalParamIR par1 = new AFormalParamLocalParamIR();
			AIdentifierPatternIR idPat1 = new AIdentifierPatternIR();
			idPat1.setName("objID");
			par1.setPattern(idPat1);
			AExternalTypeIR tyPat = new AExternalTypeIR();
			tyPat.setName("int");
			par1.setType(tyPat);
			par.add(par1);

			// Second parameter
			AFormalParamLocalParamIR par2 = new AFormalParamLocalParamIR();
			AIdentifierPatternIR idPat2 = new AIdentifierPatternIR();
			idPat2.setName("funID");
			par2.setPattern(idPat2);
			par2.setType(tyPat.clone());
			par.add(par2);

			// Third parameter
			AFormalParamLocalParamIR par4 = new AFormalParamLocalParamIR();
			AIdentifierPatternIR idPat4 = new AIdentifierPatternIR();
			idPat4.setName("nrArgs");
			par4.setPattern(idPat4);
			par4.setType(tyPat.clone());
			par.add(par4);

			// Fourth parameter
			AFormalParamLocalParamIR par3 = new AFormalParamLocalParamIR();
			AIdentifierPatternIR idPat3 = new AIdentifierPatternIR();
			idPat3.setName("");
			par3.setPattern(idPat3);
			AExternalTypeIR tyVar = new AExternalTypeIR();
			tyVar.setName("...");
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

			APlainCallStmIR plain = new APlainCallStmIR();
			
			plain.setType(new AIntNumericBasicTypeIR());
			
			plain.setName("va_start");
			
			AIdentifierVarExpIR id1 = new AIdentifierVarExpIR();
			id1.setIsLambda(false);
			id1.setIsLocal(false);
			id1.setName("args");
			id1.setType(new AIntNumericBasicTypeIR());
			plain.getArgs().add(id1);
			
			AIdentifierVarExpIR id2 = new AIdentifierVarExpIR();
			id2.setIsLambda(false);
			id2.setIsLocal(false);
			id2.setName("nr_args");
			id2.setType(new AVoidTypeIR());
			plain.getArgs().add(id2);
			
			//st.add(plain);
			
			/** One if statement pr. bus for a cpu ***/

			for(String bus : SystemArchitectureAnalysis.connectionMapStr.keySet()){

				// 1. statement
				AIfStmIR first = new AIfStmIR();

				//**** Exp part
				//AEqualsBinaryExpIR bin = new AEqualsBinaryExpIR();

				// Left side , Always the same
				AIdentifierVarExpIR id = new AIdentifierVarExpIR();
				id.setIsLambda(false);
				id.setIsLocal(true);
				id.setName("objID");
				id.setType(new AIntNumericBasicTypeIR());

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

					//					AOrBoolBinaryExpIR orBin = new AOrBoolBinaryExpIR();
					//					orBin.setLeft(binList.get(0));
					//					orBin.setRight(binList.get(1));
					//					first.setIfExp(orBin);
				}

				//**** Then part, e.g. call the specific BUS
				AReturnStmIR ret = new AReturnStmIR();
				AApplyExpIR app = new AApplyExpIR();
				
				// Set arguments
				LinkedList<SExpIR> args = new LinkedList<SExpIR>();
				//AIntLiteralExpIR v = new AIntLiteralExpIR();
				//v.setValue((long) 6);
				//args.add(v);
				
				// 1. argument
				args.add(id); 
				
				// 2. argument
				AIdentifierVarExpIR funID = new AIdentifierVarExpIR();
				funID.setIsLambda(false);
				funID.setIsLocal(true);
				funID.setName("funID");
				funID.setType(new AIntNumericBasicTypeIR());
				args.add(funID);
				
				// 3. argument
				AIdentifierVarExpIR nrArgs = new AIdentifierVarExpIR();
				nrArgs.setIsLambda(false);
				nrArgs.setIsLocal(true);
				nrArgs.setName("nrArgs");
				nrArgs.setType(new AIntNumericBasicTypeIR());
				args.add(nrArgs);
				
				// 4. argument
				AIdentifierVarExpIR ar = new AIdentifierVarExpIR();
				ar.setIsLambda(false);
				ar.setIsLocal(true);
				ar.setName("args");
				ar.setType(new AIntNumericBasicTypeIR());
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

				//first.setIfExp(orBin);
				
				//st.add(app);
				
				st.add(first);

//				st.add(first.clone());
			}
			
			body.setStatements(st);
			
			//LinkedList<SStmIR> s = ((ABlockStmIR) node.getBody()).getStatements();

			
//			AVarDeclIR args_list = new AVarDeclIR();
//			
//			AExternalTypeIR tyArgs = new AExternalTypeIR();
//			tyPat.setName("va_list");
//			
//			// set expression
//			
//			AIntLiteralExpIR ex = new AIntLiteralExpIR();
//			//ex.setValue((long) 0);
//			ex.setType(tyArgs);
//			args_list.setExp(null);
//			
//			args_list.setFinal(false);
//			
//			AIdentifierPatternIR idP = new AIdentifierPatternIR();
//			
//			idP.setName("args");
//			
//			args_list.setPattern(idP);
//			
//			args_list.setType(tyArgs);
			
//			AIdentifierVarExpIR ar = new AIdentifierVarExpIR();
//			ar.setIsLambda(false);
//			ar.setIsLocal(true);
//			ar.setName("args");
//			ar.setType(tyArgs);
			
//			args_list.setPattern(value);
			
//			body.getLocalDefs().add(args_list);
			
			//st.add(0, idVar);
			
//			body.setStatements(st);
			
			m.setBody(body);
			cl.getMethods().add(m);
			//System.out.println();

		}



	}


}
