package org.overture.codegen.vdm2c;

import java.io.File;
import java.io.IOException;

import org.junit.Assume;
import org.junit.Test;
import org.overture.codegen.vdm2c.CMakeUtil.CMakeGenerateException;

public class CasesTest extends MultiFileModelTestBase
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
	
	public void runCaseStudyTest(String submodulePath, String vdmModelSubfolder) throws IOException, InterruptedException, CMakeGenerateException
	{
		assumeCaseStudy(submodulePath);
		
		runTest(new File(submodulePath, vdmModelSubfolder));
	}

	public void assumeCaseStudy(String submodulePath)
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
}
