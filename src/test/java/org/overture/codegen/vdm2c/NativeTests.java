package org.overture.codegen.vdm2c;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.overture.codegen.vdm2c.CMakeUtil.CMakeGenerateException;
import org.overture.test.framework.ConditionalIgnoreMethodRule.ConditionalIgnore;

public class NativeTests extends NativeTestBase
{
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ExpressionForLoop() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionForLoop.vdmrt"));
		compileAndTest(new File("src/test/resources/native/forloop/ExpressionsForloop_Tests.cpp".replace('/', File.separatorChar)));
	}

	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ExpressionSeq() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionSeq.vdmrt"));
		compileAndTest(new File("src/test/resources/native/MethodNameMap.h".replace('/', File.separatorChar)), new File("src/test/resources/native/seq/ExpressionsSeq_Tests.cpp".replace('/', File.separatorChar)));
	}
	
	
//	@ConditionalIgnore(condition = HasVdmLib.class)
//	@Test
//	public void cls() throws IOException, InterruptedException,
//			CMakeGenerateException
//	{
//		generate("/Users/kel/data/into-cps/svn-intocps/WP1/T1_1_Railways/PLCP_CoSim/model_de/ModelVar.vdmrt","/Users/kel/data/into-cps/svn-intocps/WP1/T1_1_Railways/PLCP_CoSim/model_de/ModelVarBOOL.vdmrt","/Users/kel/data/into-cps/svn-intocps/WP1/T1_1_Railways/PLCP_CoSim/model_de/ModelVarINT.vdmrt");
//		compileAndTest();
//	}
	
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ExpressionLet() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionLet.vdmrt"));
		compileAndTest();
	}
	
	@ConditionalIgnore(condition = HasVdmLib.class)
	@Test
	public void ExpressionSet() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		generate(getPath("expressions/ExpressionSetForCGTesting.vdmrt"));
		compileAndTest();
	}
}
