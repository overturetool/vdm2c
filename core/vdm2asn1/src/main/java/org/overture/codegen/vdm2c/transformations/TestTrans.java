package org.overture.codegen.vdm2c.transformations;

import java.util.LinkedList;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SDeclIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.ATypeDeclIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class TestTrans extends DepthFirstAnalysisCAdaptor
{
	private  TransAssistantIR assist;

	public TestTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseADefaultClassDeclIR(ADefaultClassDeclIR node)
			throws AnalysisException
	{
		System.out.println("class name is " + node.getName());	
		
		// Get all types
		LinkedList<ATypeDeclIR> tyDecls = node.getTypeDecls();
		
		for(ATypeDeclIR tyDecl : tyDecls){
			
			SDeclIR decl = tyDecl.getDecl();
			
			
			
		}
		
		
	}
	
//	@Override
//	public void caseARecordTypeIR(ARecordTypeIR node) throws AnalysisException
//	{
//		AClassTypeIR classType = new AClassTypeIR();
//		classType.setSourceNode(node.getSourceNode());
//		classType.setName(node.getName().getName());
//
//		assist.replaceNodeWith(node, classType);
//	}
}
