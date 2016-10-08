package org.overture.codegen.vdm2c.distribution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.ASystemClassDeclIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ACallObjectStmIR;
import org.overture.codegen.ir.types.AClassTypeIR;

public class SystemArchitectureAnalysis
{

	public HashMap<String, Set<SExpIR>> distributionMap = new HashMap<String, Set<SExpIR>>();
	public Map connectionMap = new HashMap<>();

	public void initDistributionMap(String cpuName)
	{
		HashSet<SExpIR> set = new HashSet<SExpIR>();
		distributionMap.put(cpuName, set);
	}

	public void analyseSystem(List<IRStatus<PIR>> statuses)
	{

		IRStatus<PIR> status = null;
		for (IRStatus<PIR> irStatus : statuses)
		{
			if (irStatus.getIrNode() instanceof ASystemClassDeclIR)
			{
				status = irStatus;

			}
		}

		// If there is a system class
		if (status != null)
		{

			ASystemClassDeclIR systemDef = (ASystemClassDeclIR) status.getIrNode().clone();

			for (AFieldDeclIR f : systemDef.getFields())
			{

				if (f.getType() instanceof AClassTypeIR)
				{
					AClassTypeIR type = (AClassTypeIR) f.getType();

					if (type.getName().equals("CPU"))
					{
						// Initialise the distribution Map
						initDistributionMap(f.getName());
					}
				}
			}

			for (AMethodDeclIR m : systemDef.getMethods())
			{
				// Check if constrcutor
				if (m.getIsConstructor())
				{

					if (m.getBody() instanceof ABlockStmIR)
					{
						ABlockStmIR body = (ABlockStmIR) m.getBody();
						
						for (SStmIR s : body.getStatements())
						{
							if (s instanceof ACallObjectStmIR)
							{
								ACallObjectStmIR o = (ACallObjectStmIR) s;
								String cpuName = o.getDesignator().toString();
								Set<SExpIR> depSet = distributionMap.get(cpuName);
								for (SExpIR arg : o.getArgs())
								{
									depSet.add(arg);
									distributionMap.put(cpuName, depSet);
									// System.out.println("Value is " + arg);
								}
							}
						}
					}
				}
			}
		}
	}
	// if (status != null)
	// {
	// ASystemClassDeclIR systemDef = (ASystemClassDeclIR) status.getIrNode();
	// ADefaultClassDeclIR cDef = new ADefaultClassDeclIR();
	// cDef.setSourceNode(systemDef.getSourceNode());
	// cDef.setName(systemDef.getName());
	// for (AFieldDeclIR f : systemDef.getFields())
	// {
	// if (f.getType() instanceof AClassTypeIR)
	// {
	// AClassTypeIR type = (AClassTypeIR) f.getType();
	// if (type.getName().equals("CPU")
	// || type.getName().equals("BUS"))
	// {
	// continue;
	// }
	// }
	//
	// cDef.getFields().add(f.clone());
	// }
	//
	// for(AMethodDeclIR i : systemDef.getMethods())
	// {
	// cDef.getMethods().add(i.clone());
	// }
	//
	// status.setIrNode(cDef);
	// }

}
