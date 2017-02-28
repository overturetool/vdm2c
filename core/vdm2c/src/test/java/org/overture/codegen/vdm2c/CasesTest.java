package org.overture.codegen.vdm2c;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import org.junit.Assume;
import org.junit.Test;
import org.overture.codegen.vdm2c.CMakeUtil.CMakeGenerateException;

public class CasesTest extends NativeTestBase
{
	@Test
	public void fcu() throws IOException, InterruptedException, CMakeGenerateException
	{
		runCaseStudyTest("../../cases/fcu/", "Models/FCUController");
	}
	
	@Test
	public void lineFollowerRobot() throws IOException, InterruptedException, CMakeGenerateException
	{
		runCaseStudyTest("../../cases/line-follower-robot/", "Models/LFRController");
	}
	
	@Test
	public void singleWaterTank() throws IOException, InterruptedException, CMakeGenerateException
	{
		runCaseStudyTest("../../cases/single-watertank/", "Models/SingleWT/");
	}
	
	@Test
	public void threeTank() throws IOException, InterruptedException, CMakeGenerateException
	{
		runCaseStudyTest("../../cases/three-tank/", "Models/WaterTankController/");
	}
	
	@Override
	protected void assertTestsExecuted(File... tests)
	{
		// Make no assertions about tests being executed.
	}
	
	private void runCaseStudyTest(String submodulePath, String vdmModelSubfolder) throws IOException, InterruptedException, CMakeGenerateException
	{
		assumeCaseStudy(submodulePath);
		
		String[] filenames = getFilePaths(new File(submodulePath, vdmModelSubfolder), new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				return new File(dir, name).isDirectory() || name.endsWith(".vdmrt");
			}
		});
		
		Assume.assumeTrue("Could not find any VDM sources", filenames != null && filenames.length > 0);
		
		generate(filenames);
		
		File[] files = new File[filenames.length];
		
		for(int i = 0; i < filenames.length; i++)
		{
			files[i] = new File(filenames[i]);
		}
		
		compileAndTest(files);
	}

	private void assumeCaseStudy(String submodulePath)
	{
		Assume.assumeTrue("Could not find case study model."
				+ " Make sure that the corresponding submodules"
				+ " have been initialised, for example, by running"
				+ " 'git submodule update --init'", submoduleIsInitialized(submodulePath));
	}

	private boolean submoduleIsInitialized(String submodulePath)
	{
		return new File(submodulePath, ".git").exists();
	}

	@Override
	protected List<String> buildArgs(String... paths)
	{
		List<String> args = super.buildArgs(paths);
		args.add("-gc");

		return args;
	}

	@Override
	protected File getFixtureFile()
	{
		return new File("src/test/resources/garbage-collection/TestFlowFunctions.h");
	}
}
