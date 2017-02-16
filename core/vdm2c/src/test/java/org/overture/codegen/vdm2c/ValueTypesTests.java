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
	
	@Test
	public void RecordsValueSemanticsTest() throws Exception
	{
		generate(getPath("value-types/RecordsValueSemanticsTest.vdmrt"));
		compileAndTest(getTestCppFile("records/RecordsValueSemanticsTest_Tests.cpp"));		
	}
	
	@Test
	public void Tuples() throws Exception
	{
		generate(getPath("value-types/Tuples.vdmrt"));
		compileAndTest(getTestCppFile("tuples/Tuple_Tests.cpp"));
	}
}
