package org.overture.codegen.vdm2c;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.overture.codegen.vdm2c.CMakeUtil.CMakeGenerateException;

/**
 * This test checks that the examples used in one of the VDM2C papers generate
 * and compile correctly. All the examples used in this test are "external"
 * models that are believed to be suitable candidates for analysing the
 * performance of VDM2C (execution time and memory usage). This test does not
 * yet execute any of the generated code.
 * 
 * All the models ship with Overture version 2.4.6.
 * 
 */
public class PaperExampleTests extends MultiFileModelTestBase {
	
	@BeforeClass
	public static void initVdm()
	{
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
	}
	
	@Test
	public void alarm() throws Exception {
		runTest(new File("src/test/resources/vdmrt/paper-examples/alarm"));
	}
}
