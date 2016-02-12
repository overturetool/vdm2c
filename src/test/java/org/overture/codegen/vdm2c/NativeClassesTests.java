package org.overture.codegen.vdm2c;

import java.io.IOException;

import org.junit.Test;
import org.overture.codegen.vdm2c.CMakeUtil.CMakeGenerateException;
import org.overture.test.framework.ConditionalIgnoreMethodRule.ConditionalIgnore;

public class NativeClassesTests extends NativeTestBase
{

	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassValues() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("classes/ClassValue.vdmrt"));
		compileAndTest(getTestCppFile("classes/ClassValue_Tests.cpp"));
	}

	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassInstanceVariable() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("classes/ClassInstanceVariable.vdmrt"));
		compileAndTest();
	}
	
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassInstanceVariableInheritance() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("classes/ClassInstanceVariableInheritance.vdmrt"));
		compileAndTest(getTestCppFile("classes/ClassInstanceVariableInheritance_Tests.cpp"));
	}
	
	
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassInstanceVariableSeqAssign() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("classes/ClassInstanceVariableSeqAssign.vdmrt"));
		compileAndTest();
	}
	
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassInstanceVariableSeqCall() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("classes/ClassInstanceVariableSeqCall.vdmrt"));
		compileAndTest(getTestCppFile("classes/ClassInstanceVariableSeqCall_Tests.cpp"));
	}

	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassIsNotYetSpecified() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("classes/ClassIsNotYetSpecified.vdmrt"));
		compileAndTest(getTestCppFile("classes/ClassIsNotYetSpecified_external.c"),getTestCppFile("classes/ClassIsNotYetSpecified_Tests.cpp"));
	}
	
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassOp() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("classes/ClassOp.vdmrt"));
		compileAndTest();
	}
	
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassOp2() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("classes/ClassOp2.vdmrt"));
		compileAndTest();
	}
	
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassOpInheritance() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("classes/ClassOpInheritance.vdmrt"));
		compileAndTest(getTestCppFile("classes/ClassOpInheritance_Tests.cpp"));
	}
	
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassOpInheritanceOverride() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("classes/ClassOpInheritanceOverride.vdmrt"));
		compileAndTest(getTestCppFile("classes/ClassOpInheritanceOverride_Tests.cpp"));
	}
	
	
	
}
