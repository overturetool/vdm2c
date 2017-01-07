package org.overture.codegen.vdm2c;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.overture.codegen.vdm2c.CMakeUtil.CMakeGenerateException;

public class ExplicitMemoryManagementTests extends MemoryManagementTestBase {

	@Test
	public void SimpleCases() throws IOException, InterruptedException, CMakeGenerateException {
		generate(getPath("explicit-memory-management/SimpleCases.vdmrt"));
		compileAndTest(new File("src/test/resources/native/explicit-memory-management/SimpleCases_Tests.cpp".replace('/',
				File.separatorChar)));
	}
}
