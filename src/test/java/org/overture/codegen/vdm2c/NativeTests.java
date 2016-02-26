package org.overture.codegen.vdm2c;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.overture.codegen.vdm2c.CMakeUtil.CMakeGenerateException;
import org.overture.test.framework.ConditionalIgnoreMethodRule.ConditionalIgnore;

public class NativeTests extends NativeTestBase
{
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ExpressionForLoop() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionForLoop.vdmrt"));
		compileAndTest(new File("src/test/resources/native/forloop/ExpressionsForloop_Tests.cpp".replace('/', File.separatorChar)));
	}

	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ExpressionSeq() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionSeq.vdmrt"));
		compileAndTest(new File("src/test/resources/native/MethodNameMap.h".replace('/', File.separatorChar)), new File("src/test/resources/native/seq/ExpressionsSeq_Tests.cpp".replace('/', File.separatorChar)));
	}

	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ExpressionLet() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionLet.vdmrt"));
		compileAndTest(getTestCppFile("let/ExpressionsLet_Tests.cpp"));
	}

	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ExpressionBoolean() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionBoolean.vdmrt"));
		compileAndTest(getTestCppFile("boolean/ExpressionsBoolean_Tests.cpp"));
	}

	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ExpressionNumeric() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionNumeric.vdmrt"));
		compileAndTest(getTestCppFile("numeric/ExpressionsNumeric_Tests.cpp"));
	}

	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ExpressionSetForCGTesting() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionSetForCGTesting.vdmrt"));
		compileAndTest(getTestCppFile("set/ExpressionsSetForCGTesting_Tests.cpp"));
	}

	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ExpressionCases() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionCases.vdmrt"));
		compileAndTest(getTestCppFile("cases/ExpressionsCases_Tests.cpp"));
	}

	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void RemoveRTConstructs() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/RTConstructs.vdmrt"));
		compileAndTest(getTestCppFile("rt/RTConstructs_Tests.cpp"));
	}
	
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ExpressionQuote() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionQuote.vdmrt"));
		compileAndTest();
	}

	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void SubclassResponsibility() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("classes/SubclassResponsibility.vdmrt"));
		compileAndTest(getTestCppFile("classes/ClassSubclassResponsibility_Tests.cpp"));
	}
	
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void IOLibrary() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("lib/IO.vdmrt"));
		compileAndTest(getTestCppFile("classes/ClassIO_Tests.cpp"));
	}

}
