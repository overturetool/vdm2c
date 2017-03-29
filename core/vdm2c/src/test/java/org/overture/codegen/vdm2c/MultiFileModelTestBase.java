package org.overture.codegen.vdm2c;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assume;
import org.overture.codegen.vdm2c.CMakeUtil.CMakeGenerateException;

public class MultiFileModelTestBase extends NativeTestBase {

	public MultiFileModelTestBase() {
		super();
	}

	public void runTest(File modelFolder) throws IOException, InterruptedException, CMakeGenerateException {
		
		runTest(modelFolder, new LinkedList<File>());
	}
	
	public void runTest(File modelFolder, List<File> additionalCppTestFiles) throws IOException, InterruptedException, CMakeGenerateException {
		String[] filenames = getFilePaths(modelFolder, new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				return new File(dir, name).isDirectory() || name.endsWith(".vdmrt");
			}
		});
		
		Assume.assumeTrue("Could not find any VDM sources", filenames != null && filenames.length > 0);
		
		generate(filenames);
		
		compileAndTest(additionalCppTestFiles.toArray(new File[]{}));
	}

	@Override
	protected List<String> buildArgs(String... paths) {
		List<String> args = super.buildArgs(paths);
		args.add("-gc");
	
		return args;
	}

	@Override
	protected File getFixtureFile() {
		return new File("src/test/resources/garbage-collection/TestFlowFunctions.h");
	}

}