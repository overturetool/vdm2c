package org.overture.codegen.vdm2c;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.overture.codegen.vdm2c.CMakeUtil.CMakeGenerateException;

public class DistributionTests extends DistTestBase
{
	@Test
	public void Test1() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("dist/dG.vdmrt"));
		//compileAndTest(new File("src/test/resources/native/forloop/ExpressionsForloop_Tests.cpp".replace('/', File.separatorChar)));
	}
}
