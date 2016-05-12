package org.overture.codegen.vdm2c;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.overture.codegen.vdm2c.CMakeUtil.CMakeGenerateException;
import org.overture.test.framework.ConditionalIgnoreMethodRule.ConditionalIgnore;

public class ExternalTests extends NativeTestBase
{
	final static String ExternalTestsPathProperty = "vdm2c.external.tests.path";

	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void externalTest() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		String path = System.getProperty(ExternalTestsPathProperty);
		if (path == null)
			return;

		String[] filePaths = getFilePaths(new File(path), vdmRtFileFilter);
		generate(filePaths);
		compileAndTest();
	}

}


