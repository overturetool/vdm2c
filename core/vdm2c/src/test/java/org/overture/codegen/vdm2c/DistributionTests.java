package org.overture.codegen.vdm2c;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.overture.codegen.vdm2c.CMakeUtil.CMakeGenerateException;



public class DistributionTests extends DistTestBase
{
	private int delay = 5000;
	private boolean deamonFlag = false;

	// Old test
	//@Test
	public void Test1() throws IOException, InterruptedException,
	CMakeGenerateException
	{

		generate(getPath("dist/dG.vdmrt"));

		/** 1. Create the directory  **/

		// cpu1 directory
		File cpu1Dir = new File("target/test-cgen/DistributionTests/Test1/cpu1");
		copyTestFilesDist(cpu1Dir , new File("src/test/resources/distribution/test1/cpu1/distCall.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/test1/cpu1/distCall.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/test1/cpu1/CMakeLists.txt".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/test1/cpu1/PrototypeCGMacro.c".replace('/', File.separatorChar)));

		// cpu2 directory
		File cpu2Dir = new File("target/test-cgen/DistributionTests/Test1/cpu2");
		copyTestFilesDist(cpu2Dir , new File("src/test/resources/distribution/test1/cpu2/distCall.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/test1/cpu2/distCall.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/test1/cpu2/CMakeLists.txt".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/test1/cpu2/PrototypeCGMacro.c".replace('/', File.separatorChar)));

		/** 2. Run cmake pr. CPU  **/

		String cmake = findCMake();
		
		//ProcessBuilder pb = new ProcessBuilder(cmake,".");
		ProcessBuilder pb = new ProcessBuilder(cmake,"-DCMAKE_BUILD_TYPE=Debug", String.format("-DVDM_LIB_PATH=%s", (new File("../../c/vdmclib/src/main/")).getAbsolutePath()), ".");

		CMakeUtil cmakeUtil = new CMakeUtil(new File("ss"), new File("src/test/resources/CMakeLists.txt"), false);

		// cpu1
		pb.directory(cpu1Dir);
		cmakeUtil.runProcess(pb, true);
		// cpu2
		pb.directory(cpu2Dir);
		cmakeUtil.runProcess(pb, true);

		/** 3. Run make pr. CPU **/

		String make = "make";
		ProcessBuilder pb2 = new ProcessBuilder(make);

		// cpu1
		pb2.directory(cpu1Dir);
		cmakeUtil.runProcess(pb2, false);

		// cpu2
		pb2.directory(cpu2Dir);
		cmakeUtil.runProcess(pb2, false);

		/** 4. Run the exetutables **/

		// cpu2 -- async call
		String name = "cpu2Exe";
		name = "./" + name;
		ProcessBuilder pb3 = new ProcessBuilder(name);
		pb3.directory(cpu2Dir);
		pb3.start();

		Thread.sleep(delay);
		
		// cpu1 -- sync call
		cmakeUtil.run(cpu1Dir, "cpu1Exe", TEST_OUTPUT != null);

	}

	private String findCMake() {
		String cmake = "cmake";

		if (CMakeUtil.isMac())
		{
			cmake = "/usr/local/bin/cmake";
		}
		return cmake;
	}

	@Test
	public void TestAsn() throws IOException, InterruptedException,
	CMakeGenerateException
	{

		generate(getPath("dist/dG.vdmrt"));

		/** 1. Create the directory  **/

		// cpu1 directory
		File cpu1Dir = new File("target/test-cgen/DistributionTests/TestAsn/cpu1");
		copyTestFilesDist(cpu1Dir , new File("src/test/resources/distribution/testAsn/cpu1/distCall.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsn/cpu1/distCall.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsn/cpu1/CMakeLists.txt".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsn/cpu1/main.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsn/cpu1/intVal.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsn/cpu1/intVal.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsn/cpu1/asn1crt.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsn/cpu1/asn1crt.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsn/cpu1/real.c".replace('/', File.separatorChar)));

		// cpu2 directory
		File cpu2Dir = new File("target/test-cgen/DistributionTests/TestAsn/cpu2");
		copyTestFilesDist(cpu2Dir , new File("src/test/resources/distribution/testAsn/cpu2/distCall.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsn/cpu2/distCall.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsn/cpu2/CMakeLists.txt".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsn/cpu2/main.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsn/cpu2/intVal.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsn/cpu2/intVal.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsn/cpu2/asn1crt.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsn/cpu2/asn1crt.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsn/cpu2/real.c".replace('/', File.separatorChar)));

		/** 2. Run cmake pr. CPU  **/

		String cmake = findCMake();
		
		//ProcessBuilder pb = new ProcessBuilder(cmake,".");
		ProcessBuilder pb = new ProcessBuilder(cmake,"-DCMAKE_BUILD_TYPE=Debug", String.format("-DVDM_LIB_PATH=%s", (new File("../../c/vdmclib/src/main/")).getAbsolutePath()), ".");

		CMakeUtil cmakeUtil = new CMakeUtil(new File("ss"), new File("src/test/resources/CMakeLists.txt"), false);

		// cpu1
		pb.directory(cpu1Dir);
		cmakeUtil.runProcess(pb, deamonFlag);
		// cpu2
		pb.directory(cpu2Dir);
		cmakeUtil.runProcess(pb, deamonFlag);

		/** 3. Run make pr. CPU **/

		String make = "make";
		ProcessBuilder pb2 = new ProcessBuilder(make);

		// cpu1
		pb2.directory(cpu1Dir);
		cmakeUtil.runProcess(pb2, deamonFlag);

		// cpu2
		pb2.directory(cpu2Dir);
		cmakeUtil.runProcess(pb2, deamonFlag);

		/** 4. Run the exetutables **/

		// cpu2 -- async call
		String name = "cpu2Exe";
		name = "./" + name;
		ProcessBuilder pb3 = new ProcessBuilder(name);
		pb3.directory(cpu2Dir);
		pb3.start();

		Thread.sleep(delay);
		
		// cpu1 -- sync call
		boolean cpuReturnValue = cmakeUtil.run(cpu1Dir, "cpu1Exe", TEST_OUTPUT != null);

		Assert.assertTrue("Expected return value to be 0", cpuReturnValue);

	}

	@Test
	public void TestAsnProd() throws IOException, InterruptedException,
	CMakeGenerateException
	{

		generate(getPath("dist/distProd.vdmrt"));

		/** 1. Create the directory  **/

		// cpu1 directory
		File cpu1Dir = new File("target/test-cgen/DistributionTests/TestAsnProd/cpu1");
		copyTestFilesDist(cpu1Dir , new File("src/test/resources/distribution/testAsnProd/cpu1/distCall.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu1/distCall.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu1/CMakeLists.txt".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu1/main.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu1/intVal.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu1/intVal.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu1/asn1crt.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu1/asn1crt.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu2/real.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu1/serialise.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu1/serialise.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu1/asn1vdm.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu1/asn1vdm.h".replace('/', File.separatorChar)));

		// cpu2 directory
		File cpu2Dir = new File("target/test-cgen/DistributionTests/TestAsnProd/cpu2");
		copyTestFilesDist(cpu2Dir , new File("src/test/resources/distribution/testAsnProd/cpu2/distCall.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu2/distCall.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu2/CMakeLists.txt".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu2/main.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu2/intVal.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu2/intVal.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu2/asn1crt.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu2/asn1crt.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu2/real.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu2/serialise.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu2/serialise.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu2/asn1vdm.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testAsnProd/cpu2/asn1vdm.h".replace('/', File.separatorChar)));

		/** 2. Run cmake pr. CPU  **/

		String cmake = findCMake();
		//ProcessBuilder pb = new ProcessBuilder(cmake,".");
		ProcessBuilder pb = new ProcessBuilder(cmake,"-DCMAKE_BUILD_TYPE=Debug", String.format("-DVDM_LIB_PATH=%s", (new File("../../c/vdmclib/src/main/")).getAbsolutePath()), ".");

		CMakeUtil cmakeUtil = new CMakeUtil(new File("../../c/vdmclib/src/main/"), new File("src/test/resources/CMakeLists.txt"), false);

		// cpu1
		pb.directory(cpu1Dir);
		cmakeUtil.runProcess(pb, deamonFlag);
		// cpu2
		pb.directory(cpu2Dir);
		cmakeUtil.runProcess(pb, deamonFlag);

		/** 3. Run make pr. CPU **/

		String make = "make";
		ProcessBuilder pb2 = new ProcessBuilder(make);

		// cpu1
		pb2.directory(cpu1Dir);
		cmakeUtil.runProcess(pb2, deamonFlag);

		// cpu2
		pb2.directory(cpu2Dir);
		cmakeUtil.runProcess(pb2, deamonFlag);

		/** 4. Run the exetutables **/

		// cpu2 -- async call
		String name = "cpu2Exe";
		name = "./" + name;
		ProcessBuilder pb3 = new ProcessBuilder(name);
		pb3.directory(cpu2Dir);
		pb3.start();

		Thread.sleep(delay);
		
		String current = new java.io.File( "." ).getCanonicalPath();
		//System.out.println("Current dir:"+current);

		// cpu1 -- sync call
		boolean cpuReturnValue = cmakeUtil.run(cpu1Dir, "cpu1Exe", TEST_OUTPUT != null);

		Assert.assertTrue("Expected return value to be 0", cpuReturnValue);

	}

	@Test
	public void TestSysConsInitMethod() throws IOException, InterruptedException,
	CMakeGenerateException
	{

		generate(getPath("dist/distProd.vdmrt"));

		/** 1. Create the directory  **/

		// cpu1 directory
		File cpu1Dir = new File("target/test-cgen/DistributionTests/TestSysConsInitMethod/cpu1");
		copyTestFilesDist(cpu1Dir , new File("src/test/resources/distribution/testSysConsInitMethod/cpu1/distCall.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu1/distCall.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu1/CMakeLists.txt".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu1/main.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu1/intVal.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu1/intVal.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu1/asn1crt.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu1/asn1crt.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu2/real.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu1/serialise.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu1/serialise.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu1/asn1vdm.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu1/asn1vdm.h".replace('/', File.separatorChar)));

		// cpu2 directory
		File cpu2Dir = new File("target/test-cgen/DistributionTests/TestSysConsInitMethod/cpu2");
		copyTestFilesDist(cpu2Dir , new File("src/test/resources/distribution/testSysConsInitMethod/cpu2/distCall.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu2/distCall.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu2/CMakeLists.txt".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu2/main.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu2/intVal.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu2/intVal.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu2/asn1crt.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu2/asn1crt.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu2/real.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu2/serialise.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu2/serialise.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu2/asn1vdm.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testSysConsInitMethod/cpu2/asn1vdm.h".replace('/', File.separatorChar)));

		/** 2. Run cmake pr. CPU  **/

		String cmake = findCMake();
//		ProcessBuilder pb = new ProcessBuilder(cmake,".");
		ProcessBuilder pb = new ProcessBuilder(cmake,"-DCMAKE_BUILD_TYPE=Debug", String.format("-DVDM_LIB_PATH=%s", (new File("../../c/vdmclib/src/main/")).getAbsolutePath()), ".");

		CMakeUtil cmakeUtil = new CMakeUtil(new File("ss"), new File("src/test/resources/CMakeLists.txt"), false);

		// cpu1
		pb.directory(cpu1Dir);
		cmakeUtil.runProcess(pb, deamonFlag);
		// cpu2
		pb.directory(cpu2Dir);
		cmakeUtil.runProcess(pb, deamonFlag);

		/** 3. Run make pr. CPU **/

		String make = "make";
		ProcessBuilder pb2 = new ProcessBuilder(make);

		// cpu1
		pb2.directory(cpu1Dir);
		cmakeUtil.runProcess(pb2, deamonFlag);

		// cpu2
		pb2.directory(cpu2Dir);
		cmakeUtil.runProcess(pb2, deamonFlag);

		/** 4. Run the exetutables **/

		// cpu2 -- async call
		String name = "cpu2Exe";
		name = "./" + name;
		ProcessBuilder pb3 = new ProcessBuilder(name);
		pb3.directory(cpu2Dir);
		pb3.start();

		Thread.sleep(delay);
		
		// cpu1 -- sync call
		boolean cpuReturnValue = cmakeUtil.run(cpu1Dir, "cpu1Exe", TEST_OUTPUT != null);

		Assert.assertTrue("Expected return value to be 0", cpuReturnValue);

	}

	@Test
	public void TestQuotesBool() throws IOException, InterruptedException,
	CMakeGenerateException
	{

		generate(getPath("dist/distQuotes.vdmrt"));

		/** 1. Create the directory  **/

		// cpu1 directory
		File cpu1Dir = new File("target/test-cgen/DistributionTests/TestQuotesBool/cpu1");
		copyTestFilesDist(cpu1Dir , new File("src/test/resources/distribution/testQuotesBool/cpu1/distCall.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu1/distCall.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu1/CMakeLists.txt".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu1/main.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu1/basicTypes.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu1/basicTypes.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu1/asn1crt.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu1/asn1crt.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu2/real.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu1/serialise.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu1/serialise.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu1/asn1vdm.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu1/asn1vdm.h".replace('/', File.separatorChar)));

		// cpu2 directory
		File cpu2Dir = new File("target/test-cgen/DistributionTests/TestQuotesBool/cpu2");
		copyTestFilesDist(cpu2Dir , new File("src/test/resources/distribution/testQuotesBool/cpu2/distCall.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu2/distCall.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu2/CMakeLists.txt".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu2/main.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu2/basicTypes.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu2/basicTypes.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu2/asn1crt.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu2/asn1crt.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu2/real.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu2/serialise.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu2/serialise.h".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu2/asn1vdm.c".replace('/', File.separatorChar)),
				new File("src/test/resources/distribution/testQuotesBool/cpu2/asn1vdm.h".replace('/', File.separatorChar)));

		/** 2. Run cmake pr. CPU  **/

		String cmake = findCMake();
		
//		ProcessBuilder pb = new ProcessBuilder(cmake,".");
		ProcessBuilder pb = new ProcessBuilder(cmake,"-DCMAKE_BUILD_TYPE=Debug", String.format("-DVDM_LIB_PATH=%s", (new File("../../c/vdmclib/src/main/")).getAbsolutePath()), ".");

		CMakeUtil cmakeUtil = new CMakeUtil(new File("ss"), new File("src/test/resources/CMakeLists.txt"), false);

		// cpu1
		pb.directory(cpu1Dir);
		cmakeUtil.runProcess(pb, deamonFlag);
		// cpu2
		pb.directory(cpu2Dir);
		cmakeUtil.runProcess(pb, deamonFlag);

		/** 3. Run make pr. CPU **/

		String make = "make";
		ProcessBuilder pb2 = new ProcessBuilder(make);

		// cpu1
		pb2.directory(cpu1Dir);
		cmakeUtil.runProcess(pb2, deamonFlag);

		// cpu2
		pb2.directory(cpu2Dir);
		cmakeUtil.runProcess(pb2, deamonFlag);

		/** 4. Run the exetutables **/

		// cpu2 -- async call
		String name = "cpu2Exe";
		name = "./" + name;
		ProcessBuilder pb3 = new ProcessBuilder(name);
		pb3.directory(cpu2Dir);
		pb3.start();

		Thread.sleep(delay);
		
		// cpu1 -- sync call
		boolean cpuReturnValue = cmakeUtil.run(cpu1Dir, "cpu1Exe", TEST_OUTPUT != null);

		Assert.assertTrue("Expected return value to be 0", cpuReturnValue);

	}
}
