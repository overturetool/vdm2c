package org.overture.codegen.vdm2c;

import org.junit.Test;

public class ValueTypesTests extends NativeTestBase
{
	@Test
	public void Records() throws Exception
	{
		generate(getPath("value-types/Records.vdmrt"));
		compileAndTest(getTestCppFile("records/Records_Tests.cpp"));
	}
}
