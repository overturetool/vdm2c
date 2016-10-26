package org.overture.codegen.vdm2c.distribution.transformations;

import java.util.LinkedList;

import org.overture.ast.statements.AIfStm;
import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
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

public class DistTransTest extends DepthFirstAnalysisCAdaptor
{

	public DistTransTest(TransAssistantIR transformationAssistant)
	{
	}

	@Override
	public void caseAMethodDeclIR(AMethodDeclIR node) throws AnalysisException
	{
		System.out.println("Dist transformation, method name: " + node.getName());

		if(node.getName().equals("_Z8TestFuncEII")){ // _Z8TestFuncEII
			System.out.println();

			ADefaultClassDeclIR cl = node.getAncestor(ADefaultClassDeclIR.class);

			//AMethodTypeIR ty = node.getMethodType();

			//LinkedList<STypeIR> pa = (LinkedList<STypeIR>) ty.getParams().clone();

			// Create a new method: 

			AMethodDeclIR m = new AMethodDeclIR();

			m.setAsync(false);
			m.setAbstract(false);
			m.setAccess("public");
			m.setImplicit(false);
			m.setIsConstructor(false);
			m.setStatic(false);
			m.setName("send_bus");

//			LinkedList<AFormalParamLocalParamIR> par = (LinkedList<AFormalParamLocalParamIR>) node.getFormalParams().clone();

			LinkedList<AFormalParamLocalParamIR> par = new LinkedList<AFormalParamLocalParamIR>();
			
			AFormalParamLocalParamIR fpar = new AFormalParamLocalParamIR();
			AIdentifierPatternIR idPat = new AIdentifierPatternIR();
			idPat.setName("...");
			fpar.setPattern(idPat);
			AExternalTypeIR tyPat = new AExternalTypeIR();
			tyPat.setName("");
			fpar.setType(tyPat);
			
			par.add(fpar);
			
			AExternalTypeIR tyy = new AExternalTypeIR();
			tyy.setName("TVP");
			
			AMethodTypeIR mTy = new AMethodTypeIR();

			LinkedList<STypeIR> paramsTy = new LinkedList<STypeIR>();
			paramsTy.add(new AIntNumericBasicTypeIR());
			paramsTy.add(new AIntNumericBasicTypeIR());

			mTy.setParams(paramsTy);
			mTy.setResult(tyy);

			m.setMethodType(mTy.clone());
			m.setFormalParams(par);

			// New body
			ABlockStmIR body = new ABlockStmIR();
			body.setScoped(false);

			// Create the statements
			LinkedList<SStmIR> st = new LinkedList<SStmIR>();
			// 1. statement
			AIfStmIR first = new AIfStmIR();

			AOrBoolBinaryExpIR orBin = new AOrBoolBinaryExpIR();

			
			//**** Exp part
			AEqualsBinaryExpIR bin = new AEqualsBinaryExpIR();

			// Left side
			AIdentifierVarExpIR id = new AIdentifierVarExpIR();
			id.setIsLambda(false);
			id.setIsLocal(true);
			id.setName("id");
			id.setType(new AIntNumericBasicTypeIR());
			bin.setLeft(id);

			// Right side
			AIntLiteralExpIR val = new AIntLiteralExpIR();
			val.setType(new ANatNumericBasicTypeIR());
			val.setValue((long) 7);
			bin.setRight(val);

			//first.setIfExp(bin);


			//**** Then part
			AReturnStmIR ret = new AReturnStmIR();
			AApplyExpIR app = new AApplyExpIR();
			// Set args
			LinkedList<SExpIR> args = new LinkedList<SExpIR>();
			AIntLiteralExpIR v = new AIntLiteralExpIR();
			v.setValue((long) 6);
			args.add(v);
			app.setArgs(args);
			// Type
			app.setType(new AIntNumericBasicTypeIR());
			// Root
			AIdentifierVarExpIR idVar = new AIdentifierVarExpIR();
			idVar.setIsLambda(false);
			idVar.setIsLocal(false);
			idVar.setName("UART_send");
			// set method type


			idVar.setType(mTy.clone());

			app.setRoot(idVar);

			ret.setExp(app);

			first.setThenStm(ret);

			orBin.setLeft(bin.clone());
			orBin.setRight(bin.clone());

			first.setIfExp(orBin);

			st.add(first);

			body.setStatements(st);

			//LinkedList<SStmIR> s = ((ABlockStmIR) node.getBody()).getStatements();

			m.setBody(body);

			cl.getMethods().add(m);

			System.out.println();

		}



	}

	//	@Override
	//	public void caseADefaultClassDeclIR(ADefaultClassDeclIR node)
	//			throws AnalysisException
	//	{
	//		
	//		//AClassHeaderDeclIR par = node.getAncestor(AClassHeaderDeclIR.class);
	//		//par.apply(this);
	//		//System.out.println("Class name: " + node.getName());
	//	}

	//	@Override
	//	public void caseAArrayDeclIR(AArrayDeclIR node) throws AnalysisException
	//	{
	//		// TODO Auto-generated method stub
	//		super.caseAArrayDeclIR(node);
	//		
	//		System.out.println(node.getName());
	//		
	//		
	//	}


}
