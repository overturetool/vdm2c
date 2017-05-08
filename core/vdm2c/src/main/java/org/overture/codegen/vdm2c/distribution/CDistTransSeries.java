package org.overture.codegen.vdm2c.distribution;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.CGen;
import org.overture.codegen.vdm2c.distribution.transformations.AddIncludesTrans;
import org.overture.codegen.vdm2c.distribution.transformations.CallFuncMacroExpTrans;
import org.overture.codegen.vdm2c.distribution.transformations.GenerateClassDispatchTrans;
import org.overture.codegen.vdm2c.distribution.transformations.GenerateGetResTrans;
import org.overture.codegen.vdm2c.distribution.transformations.GenerateSendBusTrans;
import org.overture.codegen.vdm2c.distribution.transformations.TransformRemoteObject;

public class CDistTransSeries
{

	private CGen codeGen;

	public CDistTransSeries(CGen codeGen)
	{
		this.codeGen = codeGen;
	}

	public List<DepthFirstAnalysisAdaptor> consAnalyses()
	{
		TransAssistantIR transAssistant = codeGen.getTransAssistant();

		// Set up order and construction of transformations
		List<DepthFirstAnalysisAdaptor> transformations = new LinkedList<DepthFirstAnalysisAdaptor>();
		
		//transformations.add(new DistTransTest(transAssistant)); // used for testing
		transformations.add(new GenerateSendBusTrans(transAssistant));
		transformations.add(new GenerateGetResTrans(transAssistant));
		transformations.add(new CallFuncMacroExpTrans(transAssistant));
		transformations.add(new TransformRemoteObject(transAssistant));
		//transformations.add(new GenerateDepObjId(transAssistant)); // not used, but keep 
		//transformations.add(new GenerateDMTrans(transAssistant)); // not used, but keep
		transformations.add(new GenerateClassDispatchTrans(transAssistant));
		transformations.add(new AddIncludesTrans(transAssistant));

		return transformations;
	}

}
