package org.overture.codegen.vdm2c;

import java.io.IOException;

import org.junit.Test;
import org.overture.codegen.vdm2c.CMakeUtil.CMakeGenerateException;


public class Visualization extends NativeTestBase
{
 	@Test
	public void VisualizeIRAST() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		//generate(getPath("set/ExpressionSetForCGTesting.vdmrt"));
		generate(getPath("other/VisualizeIRAST.vdmrt"));
	}
}

