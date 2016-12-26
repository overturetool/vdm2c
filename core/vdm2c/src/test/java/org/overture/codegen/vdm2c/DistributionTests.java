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
		
		File root2 = new File("/Users/Miran/Documents/C_codegen/vdm2c/core/vdm2c/target/test-cgen/DistributionTests/Test1/cpu1");
		copyTestFiles2(root2,new File("src/test/resources/distribution/test1/cpu1/distCall.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/test1/cpu1/distCall.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/test1/cpu1/CMakeLists.txt".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/test1/cpu1/PrototypeCGMacro.c".replace('/', File.separatorChar)));
		
		
		// Run cmake for one CPU
		String cmake = "cmake";
		cmake = "/usr/local/bin/cmake";
		ProcessBuilder pb = new ProcessBuilder(cmake,".");
		pb.directory(root2);
		CMakeUtil cmakeUtil = new CMakeUtil(new File("ss"), new File("src/test/resources/CMakeLists.txt"), false);
		cmakeUtil.runProcess(pb, true);
		
		//async
		String name = "cpu2Exe";

		name = "./" + name;

		ProcessBuilder pb3 = new ProcessBuilder(name);
		pb3.directory(root2);
		pb3.start();
		
		// Run make
		String make = "make";
		ProcessBuilder pb2 = new ProcessBuilder(make);

		pb2.directory(root2);

		cmakeUtil.runProcess(pb2, false);

		// Execute the process
		
		cmakeUtil.run(root2, "cpu1Exe", TEST_OUTPUT != null);
	
	}
}
