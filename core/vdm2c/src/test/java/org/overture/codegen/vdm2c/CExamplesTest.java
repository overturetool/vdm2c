package org.overture.codegen.vdm2c;

import java.io.IOException;

import org.junit.Test;
import org.overture.codegen.vdm2c.CMakeUtil.CMakeGenerateException;
import org.overture.test.framework.ConditionalIgnoreMethodRule.ConditionalIgnore;

//Useful for testing the CLI to the code generator.

public class CExamplesTest extends BaseGeneratorTest
{
	@ConditionalIgnore(condition = HasVdm.class)
	@Test
	public void ExpressionForLoop() throws IOException, InterruptedException,
			CMakeGenerateException
	{
		//Example.
		
		String outputFolder = "";
		
		CGenMain.main(new String[] { "--quiet", "-dest", outputFolder,
				getPath("expressions/ExpressionForLoop.vdmrt") });

	}
}
