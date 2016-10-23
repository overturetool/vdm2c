package org.overture.codegen.vdm2c.distribution.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newExternalType;

import java.util.LinkedList;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.expressions.ABoolLiteralExpIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.distribution.SystemArchitectureAnalysis;
import org.overture.codegen.vdm2c.extast.declarations.AAnonymousStruct;
import org.overture.codegen.vdm2c.extast.declarations.AArrayDMDeclIR;
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

		if(cl.getTag()!=null){
			String cpu = cl.getTag().toString();
			
			LinkedList<AFieldDeclIR> depObjs = SystemArchitectureAnalysis.systemDeployedObjects;

			if(cl.getName().equals(SystemArchitectureAnalysis.systemName)){

				AClassHeaderDeclIR header = cl.getAncestor(AClassHeaderDeclIR.class);

				/** Init **/
				String name = "DM";
				AArrayDMDeclIR arrayDclInit = new AArrayDMDeclIR();
				arrayDclInit.setStatic(null);
				arrayDclInit.setName(name);
				arrayDclInit.setType(newExternalType("extern bool"));
				arrayDclInit.setSize(SystemArchitectureAnalysis.systemDeployedObjects.size());				
				header.setArrDMinit(arrayDclInit);

				/** The map **/
				AArrayDMDeclIR arrayDcl = new AArrayDMDeclIR();
				arrayDcl.setStatic(null);
				arrayDcl.setName(name);
				arrayDcl.setType(newExternalType("bool"));
				arrayDcl.setSize(SystemArchitectureAnalysis.systemDeployedObjects.size());

				// Add deployed objects information

				LinkedList<Boolean> cpuDM = SystemArchitectureAnalysis.DM.get(cpu);
				AAnonymousStruct structEntry = new AAnonymousStruct();

				for (Boolean val : cpuDM){
					ABoolLiteralExpIR b = new ABoolLiteralExpIR();
					b.setValue(val);
					b.setType(new ABoolBasicTypeIR());
					structEntry.getExp().add(b);
				}
				arrayDcl.getInitial().add(structEntry);
				header.setArrDM(arrayDcl);
			}
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
