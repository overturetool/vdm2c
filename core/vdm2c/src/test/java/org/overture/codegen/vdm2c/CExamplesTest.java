package org.overture.codegen.vdm2c;

import java.io.File;
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
		String outputFolder = new File("target/test-cgen/"
				+ CExamplesTest.class.getSimpleName()
				+ "/ExpressionForLoop".replace('/', File.separatorChar)).getAbsolutePath();

		CGenMain.main(new String[] { "-dest", outputFolder,
				getPath("expressions/ExpressionForLoop.vdmrt") });

	}
}
