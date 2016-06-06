package org.overture.codegen.vdm2c.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.name.ATokenNameIR;
import org.overture.codegen.ir.statements.ACyclesStmIR;
import org.overture.codegen.ir.statements.ADurationStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class IgnoreVDMUnitTrans extends DepthFirstAnalysisCAdaptor
{
	public TransAssistantIR assist;

	public IgnoreVDMUnitTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseADefaultClassDeclIR(ADefaultClassDeclIR node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseADefaultClassDeclIR(node);
		
		ADefaultClassDeclIR nodeclone = node.clone();
		
		for(ATokenNameIR i : nodeclone.getSuperNames())
		{
			if(i.getName().contains("TestCase"))
			{
				node.parent().removeChild(node);
				return;
			}
		}
		
		if(node.getName().contains("Test") ||
				node.getName().contains("TestCase") ||
				node.getName().contains("TestSuite") ||
				node.getName().contains("TestListener") ||
				node.getName().contains("TestResult") ||
				node.getName().contains("TestRunner") ||
				node.getName().contains("Throwable") ||
				node.getName().contains("Error") ||
				node.getName().contains("AssertionFailedError") ||
				node.getName().contains("Assert"))
		{
			node.parent().removeChild(node);
			return;
		}
		
		
			
	}
}
