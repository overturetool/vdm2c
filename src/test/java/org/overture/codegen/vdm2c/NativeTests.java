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
}
