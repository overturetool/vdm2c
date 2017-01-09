package org.overture.codegen.vdm2c.distribution;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newAssignment;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newIdentifier;
import static org.overture.codegen.vdm2c.utils.CTransUtil.newInternalMethod;

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
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.ASystemClassDeclIR;
import org.overture.codegen.ir.expressions.AEnumSetExpIR;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.ANewExpIR;
import org.overture.codegen.ir.name.ATokenNameIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ACallObjectStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AVoidTypeIR;

public class SystemArchitectureAnalysis
{

	public static HashMap<String, Set<SExpIR>> distributionMap = new HashMap<String, Set<SExpIR>>();
	public static Map<String, AEnumSetExpIR> connectionMap = new HashMap<String, AEnumSetExpIR>();
	public static LinkedList<AFieldDeclIR> systemDeployedObjects = new LinkedList<AFieldDeclIR>();
	public static String systemName;
	public static Map<String,LinkedList<Boolean>> DM = new HashMap<String, LinkedList<Boolean>>();

	// String versions
	public static LinkedList<String> systemDeployedObjectsStr = new LinkedList<String>();
	public static Map<String, Set<String>> connectionMapStr = new HashMap<String,Set<String>>();
	public static HashMap<String, Set<String>> distributionMapStr = new HashMap<String, Set<String>>();

	public static Set<String> systemClasses = new HashSet<String>();

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
								if(!distributionMap.keySet().contains(cpuName))
									continue;

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

	public void generateMapStrVer(){

		for(AFieldDeclIR field : systemDeployedObjects){
			systemDeployedObjectsStr.add(field.getName().toString());
		}

		for(String bus : connectionMap.keySet()){

			AEnumSetExpIR cpus = connectionMap.get(bus);

			Set<String> cpuList = new HashSet<String>();

			for(SExpIR c : cpus.getMembers()){
				cpuList.add(c.toString());
			}
			connectionMapStr.put(bus,cpuList);
			//systemDeployedObjectsStr.add(field.getName().toString());
		}

		for(String cpu : distributionMap.keySet()){

			Set<String> objList = new HashSet<String>();

			for(SExpIR obj : distributionMap.get(cpu)){
				objList.add(obj.toString());
			}
			distributionMapStr.put(cpu, objList);
		}

		String cpu = "cpu1";
		// the algorithm
		for(String bus : connectionMapStr.keySet()){

			Set<String> cpuList = connectionMapStr.get(bus);

			if(cpuList.contains(cpu)){
				for(String c : cpuList){
					if(c.equals(cpu)) continue;

					// get deployed object of a given cpu

					Set<String> depObj = distributionMapStr.get(c);

					for(String dobj : depObj){
						// Add id to if for this specific bus
						int id = systemDeployedObjectsStr.indexOf(dobj);
					}
				}
			}
			// Create the specific bus call
			String bus_name = bus;
		}
		//for(bus : connectionMap.get(key))
		//System.out.println();
	}

	static void createInitMethod(ADefaultClassDeclIR node) throws AnalysisException
	{
		ABlockStmIR body = new ABlockStmIR();

		String cpuName = node.getTag().toString();

		for (AFieldDeclIR field : node.getFields())
		{
			if (field.getFinal() && field.getInitial() != null)
			{
				body.getStatements().add(newAssignment(newIdentifier(field.getName(), null), field.getInitial()));
			}
		}
		
		//if(node.getMethods().get(0).getBody().clone() instanceof ABlockStmIR)
		ABlockStmIR consMethodBody = (ABlockStmIR) node.getMethods().get(0).getBody().clone();

		for(SStmIR s : consMethodBody.getStatements()){
			if (s instanceof ACallObjectStmIR)
			{
				ACallObjectStmIR o = (ACallObjectStmIR) s;
				String objName = o.getDesignator().toString();
				
				if(distributionMapStr.get(cpuName).contains(objName))
					body.getStatements().add(s.clone());
			}
		}

		//In case there is nothing to initialize, we still want the functions to have a body.  See comment below.
		body.getStatements().add(new AReturnStmIR());

		//Emit init function even if no value fields are present.  Simplifies FMU export.
		AMethodDeclIR method = newInternalMethod(node.getTag().toString() + "_init", body, new AVoidTypeIR(), false);
		method.setAccess("public");
		node.getMethods().add(method);
	}

	public static void addCpuInitMethod(List<IRStatus<PIR>> statuses)
	{

		for (IRStatus<PIR> irStatus : statuses)
		{

			//First remove all VDMUnited.vdmrt-related classes.
			if (irStatus.getIrNode() instanceof ADefaultClassDeclIR)
			{
				ADefaultClassDeclIR cl = (ADefaultClassDeclIR) irStatus.getIrNode();

				if(cl.getTag() != null)
					try
				{
						createInitMethod(cl);
				} catch (AnalysisException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
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
