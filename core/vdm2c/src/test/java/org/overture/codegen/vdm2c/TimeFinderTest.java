package org.overture.codegen.vdm2c;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class TimeFinderTest
{
	@Test
	public void timeMap()
	{
		Settings.dialect = Dialect.VDM_RT;
		
		File file = new File("src/test/resources/vdmrt/expressions/ExpressionTime.vdmrt");
		
		TypeCheckResult<List<SClassDefinition>> res = TypeCheckerUtil.typeCheckPp(file);
		
		Assert.assertTrue("Expected model to be type correct", res.errors.isEmpty());
		
		Map<String, Boolean> hasTimeMap = TimeFinder.computeTimeMap(res.result);

		Assert.assertEquals("Got wrong number of classes", 3, hasTimeMap.size());
		
		Assert.assertFalse("Expected class not to use 'time'", hasTimeMap.get("A"));
		Assert.assertTrue("Expected class to use 'time'", hasTimeMap.get("B"));
		Assert.assertFalse("Expected class not to use 'time'", hasTimeMap.get("C"));
	}
}
