package org.overture.codegen.vdm2c;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;

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
	
	@Override
	protected void assertTestsExecuted(File... tests) {
		
		// For now, we don't require any tests be executed.
	}
	
	@Test
	public void alarm() throws Exception {
		runTest(new File("src/test/resources/vdmrt/paper-examples/alarm"));
	}
	
	@Test
	public void cashDispenser() throws Exception {
		
		List<File> extraCppFiles = Collections.singletonList(new File("src/test/resources/native/paper-examples/cash-dispenser-extension.c"));
		
		runTest(new File("src/test/resources/vdmrt/paper-examples/cash-dispenser"), extraCppFiles);
	}
	
	@Test
	public void pacemaker() throws Exception {
		
		List<File> extraCppFiles = Collections.singletonList(new File("src/test/resources/native/paper-examples/pacemaker-extension.c"));
		runTest(new File("src/test/resources/vdmrt/paper-examples/pacemaker"), extraCppFiles);
	}
	
	@Test
	public void bubblesort() throws Exception {
		runTest(new File("src/test/resources/vdmrt/paper-examples/bubblesort"));
	}
}
