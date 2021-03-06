package org.overture.codegen.vdm2c.distribution.transformations;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

		if (node.getTag() != null){
			String cpu_name = node.getTag().toString();

			AClassHeaderDeclIR header = node.getAncestor(AClassHeaderDeclIR.class);

			List<CGenClonableString> includes = (List<CGenClonableString>) header.getIncludes();
			
			// create a set of all busses in system
			
			Map<String, Set<String>> map = SystemArchitectureAnalysis.connectionMapStr;
			
			for(String bus : SystemArchitectureAnalysis.connectionMapStr.keySet()){
				if(SystemArchitectureAnalysis.connectionMapStr.get(bus).contains(cpu_name)){
					CGenClonableString std_arge = new CGenClonableString(bus);
					includes.add(std_arge);
				}
			}
			header.setIncludes(includes);
		}
		else
		{
			AClassHeaderDeclIR header = node.getAncestor(AClassHeaderDeclIR.class);

			List<CGenClonableString> includes = (List<CGenClonableString>) header.getIncludes();

			//CGenClonableString std_arge = new CGenClonableString("bus");
			//includes.add(std_arge);

			CGenClonableString sysName = new CGenClonableString(SystemArchitectureAnalysis.systemName);
			includes.add(sysName);

			header.setIncludes(includes);
		}
	}


}
