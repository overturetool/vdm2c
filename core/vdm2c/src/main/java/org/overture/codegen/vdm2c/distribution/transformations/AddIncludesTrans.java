package org.overture.codegen.vdm2c.distribution.transformations;

import java.util.List;
import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.ast.CGenClonableString;
import org.overture.codegen.vdm2c.distribution.SystemArchitectureAnalysis;
import org.overture.codegen.vdm2c.extast.declarations.AClassHeaderDeclIR;

public class AddIncludesTrans extends DepthFirstAnalysisCAdaptor
{

	public AddIncludesTrans(TransAssistantIR transformationAssistant)
	{
	}

	@Override
	@SuppressWarnings("unchecked")
	public void caseADefaultClassDeclIR(ADefaultClassDeclIR node)
			throws AnalysisException
	{
		AClassHeaderDeclIR header = node.getAncestor(AClassHeaderDeclIR.class);
		
		List<CGenClonableString> includes = (List<CGenClonableString>) header.getIncludes();

		CGenClonableString std_arge = new CGenClonableString("distCall");
		includes.add(std_arge);

		if(!node.getName().equals(SystemArchitectureAnalysis.systemName)){
			CGenClonableString sysName = new CGenClonableString(SystemArchitectureAnalysis.systemName);
			includes.add(sysName);
		}

		header.setIncludes(includes);
	}


}
