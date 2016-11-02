package org.overture.codegen.vdm2c;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.overture.codegen.vdm2c.CMakeUtil.CMakeGenerateException;

public class NativeTests extends NativeTestBase
{
	@Test
	public void ExpressionForLoop() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionForLoop.vdmrt"));
		compileAndTest(new File("src/test/resources/native/forloop/ExpressionsForloop_Tests.cpp".replace('/', File.separatorChar)));
	}

	@Test
	public void ExpressionSeq() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionSeq.vdmrt"));
		compileAndTest(new File("src/test/resources/native/MethodNameMap.h".replace('/', File.separatorChar)), new File("src/test/resources/native/seq/ExpressionsSeq_Tests.cpp".replace('/', File.separatorChar)));
	}

	@Test
	public void ExpressionLet() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionLet.vdmrt"));
		compileAndTest(getTestCppFile("let/ExpressionsLet_Tests.cpp"));
	}
	
	@Test
	public void ExpressionLetBeSt() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionLetBeSt.vdmrt"));
		compileAndTest(getTestCppFile("let/ExpressionsLetBeSt_Tests.cpp"));
	}
	
	@Test
	public void ExpressionMap() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionMap.vdmrt"));
		compileAndTest(getTestCppFile("map/ExpressionsMap_Tests.cpp"));
	}


	@Test
	public void ExpressionBoolean() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionBoolean.vdmrt"));
		compileAndTest(getTestCppFile("boolean/ExpressionsBoolean_Tests.cpp"));
	}

	@Test
	public void ExpressionNumeric() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionNumeric.vdmrt"));
		compileAndTest(getTestCppFile("numeric/ExpressionsNumeric_Tests.cpp"));
	}

	@Test
	public void ExpressionSetForCGTesting() throws IOException,
			InterruptedException, CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionSetForCGTesting.vdmrt"));
		compileAndTest(getTestCppFile("set/ExpressionsSetForCGTesting_Tests.cpp"));
	}

	@Test
	public void ExpressionCases() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionCases.vdmrt"));
		compileAndTest(getTestCppFile("cases/ExpressionsCases_Tests.cpp"));
	}

	@Test
	public void RemoveRTConstructs() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/RTConstructs.vdmrt"));
		compileAndTest(getTestCppFile("rt/RTConstructs_Tests.cpp"));
	}
	
	@Test
	public void ExpressionQuote() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionQuote.vdmrt"));
		compileAndTest(getTestCppFile("quote/ExpressionQuote_Tests.cpp"));
	}
	
	@Test
	public void ExpressionComprehensions() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionComprehensions.vdmrt"));
		compileAndTest(getTestCppFile("comprehensions/ExpressionComprehensions_Tests.cpp"));
	}

	@Test
	public void SubclassResponsibility() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("classes/SubclassResponsibility.vdmrt"));
		compileAndTest(getTestCppFile("classes/ClassSubclassResponsibility_Tests.cpp"));
	}
	
	@Test
	public void OtherIOLib() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("other/IOLib.vdmrt"), getPath("lib/IO.vdmrt"));
		compileAndTest(getTestCppFile("classes/ClassIO_Tests.cpp"));
	}
	
	@Test
	public void OtherIgnoreVDMUnit() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("other/VDMUnitTest.vdmrt"), getPath("lib/VDMUnit.vdmrt"), getPath("lib/IO.vdmrt"));
		compileAndTest(getTestCppFile("other/IgnoreVDMUnit_Tests.cpp"));
	}
	
	@Test
	public void ExpressionRecord() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionRecord.vdmrt"));
//		compileAndTest();
	}
	
	@Test
	public void ExpressionSelf() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionSelf.vdmrt"));
		compileAndTest(getTestCppFile("self/ExpressionsSelf_Tests.cpp"));
	}
	
	@Test
	public void ExpressionIs() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionIs.vdmrt"));
		compileAndTest(getTestCppFile("is/ExpressionsIs_Tests.cpp"));
	}
}
