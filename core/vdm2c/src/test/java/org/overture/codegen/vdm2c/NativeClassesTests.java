package org.overture.codegen.vdm2c;

import java.io.IOException;

import org.junit.Test;
import org.overture.codegen.vdm2c.CMakeUtil.CMakeGenerateException;
import org.overture.test.framework.ConditionalIgnoreMethodRule.ConditionalIgnore;

public class NativeClassesTests extends NativeTestBase
{

	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassValue() throws IOException, InterruptedException,
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
		compileAndTest(getTestCppFile("classes/ClassInstanceVariable_Tests.cpp"));
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
		compileAndTest(getTestCppFile("classes/ClassInstanceVariableSeqAssign_Tests.cpp"));
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
	
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassStaticInstanceVariable() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("classes/ClassStaticInstanceVariable.vdmrt"));
		compileAndTest(getTestCppFile("classes/ClassStaticInstanceVariable_Tests.cpp"));
	}
	
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassStaticMethodAccess() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("classes/ClassStatic.vdmrt"));
		compileAndTest(getTestCppFile("classes/ClassStaticMethodAccess_Tests.cpp"));
	}
	
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassFieldAccess() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("classes/ClassFieldAccess.vdmrt"));
		compileAndTest(getTestCppFile("classes/ClassFieldAccess_Tests.cpp"));
	}
	
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassPublicFieldsTMP() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("classes/ClassPublicFieldsTMP.vdmrt"));
		compileAndTest();
	}
	
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassFunCall() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("classes/ClassFun.vdmrt"));
		compileAndTest(getTestCppFile("classes/ClassStaticFun_Tests.cpp"));
	}
	
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassOpOpCall() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("classes/ClassOpOpCall.vdmrt"));
		compileAndTest();
	}
	
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassVarOpOpCall() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("classes/ClassVarOpOpCall.vdmrt"));
		compileAndTest();
	}

	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassVarOpOpCall2() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("classes/ClassVarOpOpCall2.vdmrt"));
		compileAndTest();
	}
	
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassIfNotEq() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("classes/ClassIfNotEq.vdmrt"));
		compileAndTest();
	}
	
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassOpOverride() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("classes/ClassOpOverride.vdmrt"));
		compileAndTest();
	}
	
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassOpOverrideMiddle() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("classes/ClassOpOverrideMiddle.vdmrt"));
		compileAndTest();
	}
	
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ClassValueInheritance() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("classes/ClassValueInheritance.vdmrt"));
		compileAndTest();
	}	
}
