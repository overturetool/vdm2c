package org.overture.codegen.vdm2c.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.utils.CTransUtil;
import org.overture.codegen.vdm2c.utils.NameConverter;

public class RenameMathLibraryTrans extends DepthFirstAnalysisCAdaptor
{

	public RenameMathLibraryTrans(TransAssistantIR transformationAssistant)
	{
	}
	
	@Override
	public void caseADefaultClassDeclIR(ADefaultClassDeclIR node) throws AnalysisException {
		// TODO Auto-generated method stub
		
		if(node.getName().equals("MATH"))
		{
			node.setName("VdmMath");
		}
		super.caseADefaultClassDeclIR(node);
	}

}
