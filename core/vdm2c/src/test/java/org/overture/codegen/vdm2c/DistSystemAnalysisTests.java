package org.overture.codegen.vdm2c;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.node.INode;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.vdm2c.distribution.SystemArchitectureAnalysis;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class DistSystemAnalysisTests
{
	@Test
	public void distAnalysis1() throws AnalysisException
	{
		// Run the generator mon the ressource
		Settings.dialect = Dialect.VDM_RT;
		File file = new File("src/test/resources/vdmrt/dist/dG.vdmrt");
		TypeCheckResult<List<SClassDefinition>> res = TypeCheckerUtil.typeCheckRt(file);
		CGenMain.distGen = true;
		List<SClassDefinition> ast = res.result;
		CGen cGen = new CGen();
		List<INode> filter = CodeGenBase.getNodes(ast);
		cGen.generate(filter);
		
		// Get the generated distribution map
		HashMap<String, Set<String>> distMap = SystemArchitectureAnalysis.distributionMapStr;
		
		// Create the expected distribution map
		HashMap<String, Set<String>> expectedMap = new HashMap<String,Set<String>>();
		HashSet<String> expCpu1 = new HashSet<String>();
		expCpu1.add("a1");
		expCpu1.add("b");
		HashSet<String> expCpu2 = new HashSet<String>();
		expCpu2.add("a2");
		expectedMap.put("cpu1", expCpu1);
		expectedMap.put("cpu2", expCpu2);

		// Run tests
		Assert.assertTrue("Expected model to be type correct", res.errors.isEmpty());
		Assert.assertEquals("Got wrong number of cpus", 2 , distMap.size());
		Assert.assertTrue("Maps not equal", distMap.equals(expectedMap));

	}
}
