package org.overture.codegen.vdm2c.distribution.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newExternalType;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.expressions.ABoolLiteralExpIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.distribution.SystemArchitectureAnalysis;
import org.overture.codegen.vdm2c.extast.declarations.AAnonymousStruct;
import org.overture.codegen.vdm2c.extast.declarations.AArrayDeclIR;
import org.overture.codegen.vdm2c.extast.declarations.AClassHeaderDeclIR;

public class GenerateDMTrans extends DepthFirstAnalysisCAdaptor
{

	public GenerateDMTrans(TransAssistantIR transformationAssistant)
	{
	}

//	@Override
//	public void caseAMethodDeclIR(AMethodDeclIR node) throws AnalysisException
//	{
//		if(node.getName().equals(SystemArchitectureAnalysis.systemName + "_static_init")){
//		//System.out.println("Dist transformation, method name: " + node.getName());
//		}
//	}
	
	@Override
	public void caseADefaultClassDeclIR(ADefaultClassDeclIR node)
			throws AnalysisException
	{
		
		ADefaultClassDeclIR cl = node;
		
		if(cl.getName().equals(SystemArchitectureAnalysis.systemName)){
		
		AClassHeaderDeclIR header = cl.getAncestor(AClassHeaderDeclIR.class);
		
		/** Init **/
		String name = "DM";
		AArrayDeclIR arrayDclInit = new AArrayDeclIR();
		arrayDclInit.setStatic(null);
		arrayDclInit.setName(name);
		arrayDclInit.setType(newExternalType("extern bool"));
		arrayDclInit.setSize(SystemArchitectureAnalysis.systemDeployedObjects.size());				
		header.setArrDMinit(arrayDclInit);
		
		/** The map **/
		AArrayDeclIR arrayDcl = new AArrayDeclIR();
		arrayDcl.setStatic(null);
		arrayDcl.setName(name);
		arrayDcl.setType(newExternalType("extern bool"));
		arrayDcl.setSize(SystemArchitectureAnalysis.systemDeployedObjects.size());
		
		// Add deployed objects information
		ABoolLiteralExpIR b = new ABoolLiteralExpIR();
		
		b.setValue(true);
		b.setType(new ABoolBasicTypeIR());
		
		
		ABoolLiteralExpIR c = new ABoolLiteralExpIR();
		
		c.setValue(false);
		c.setType(new ABoolBasicTypeIR());
		
		AAnonymousStruct structEntry = new AAnonymousStruct();

		structEntry.getExp().add(b);
		structEntry.getExp().add(c);
		//arrayDcl.setInitial(structEntry);
		
		arrayDcl.getInitial().add(structEntry);
				
		header.setArrDM(arrayDcl);
		
		//header.get
		
		//System.out.println();
		}
	}
	
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
