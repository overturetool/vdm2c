package org.overture.codegen.vdm2c.distribution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
import org.overture.codegen.ir.expressions.AEnumSetExpIR;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.ANewExpIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ACallObjectStmIR;
import org.overture.codegen.ir.types.AClassTypeIR;

public class SystemArchitectureAnalysis
{

	public static HashMap<String, Set<SExpIR>> distributionMap = new HashMap<String, Set<SExpIR>>();
	public static Map<String, AEnumSetExpIR> connectionMap = new HashMap<String, AEnumSetExpIR>();
	public static LinkedList<AFieldDeclIR> systemDeployedObjects = new LinkedList<AFieldDeclIR>();
	public static String systemName;
	public static Map<String,LinkedList<Boolean>> DM = new HashMap<String, LinkedList<Boolean>>();

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

			systemName = systemDef.getName();

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
					else if (type.getName().equals("BUS"))
					{
						// Initialise the connection Map
						if (f.getInitial() instanceof ANewExpIR)
						{
							ANewExpIR init = (ANewExpIR) f.getInitial();

							SExpIR args = init.getArgs().get(2);

							if (args instanceof AEnumSetExpIR)
							{
								AEnumSetExpIR cpu_args = (AEnumSetExpIR) args;
								connectionMap.put(f.getName(), cpu_args);
							}
						}
					}
					else{
						systemDeployedObjects.add(f);
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
								}
							}
						}
					}
				}
			}
		}
	}

	public void generateDM(){

		for(String cpu: distributionMap.keySet()){ // pr. cpu
			LinkedList<Boolean> li = new LinkedList<Boolean>();
			li.add(true); // first value is always true
			for(AFieldDeclIR obj: systemDeployedObjects){ // pr. deployed object
				Set<SExpIR> deployedObjs = distributionMap.get(cpu);
				Boolean val = false;
				for(SExpIR dep : deployedObjs)
					if(dep.toString().equals(obj.getName().toString())) 
						val = true;
				li.add(val);
			}
			DM.put(cpu, li);
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
