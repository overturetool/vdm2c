package org.overture.codegen.vdm2c;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.overture.codegen.vdm2c.CMakeUtil.CMakeGenerateException;
import org.overture.test.framework.ConditionalIgnoreMethodRule.ConditionalIgnore;

public class CExamplesTest extends BaseGeneratorTest
{
	@ConditionalIgnore(condition = HasVdm.class)
	@Test
	public void a()
	{
		CGenMain.main(new String[] { "-dest", outputFolder,
				getPath("classes/A.vdmrt") });
	}

	@ConditionalIgnore(condition = HasVdm.class)
	@Test
	public void c()
	{
		CGenMain.main(new String[] { "-dest", outputFolder,
				getPath("classes/C.vdmrt") });
	}

	@ConditionalIgnore(condition = HasVdm.class)
	@Test
	public void b()
	{
		CGenMain.main(new String[] { "-dest", outputFolder,
				getPath("classes/A.vdmrt"), getPath("classes/C.vdmrt"),
				getPath("classes/B.vdmrt") });
	}

	@ConditionalIgnore(condition = HasVdm.class)
	@Test
	public void d()
	{
		CGenMain.main(new String[] { "-dest", outputFolder,
				getPath("classes/D.vdmrt"), getPath("classes/A.vdmrt") });
	}

	@ConditionalIgnore(condition = HasVdm.class)
	@Test
	public void call()
	{
		CGenMain.main(new String[] { "-dest", outputFolder,
				getPath("classes/Call.vdmrt") });
	}

	@ConditionalIgnore(condition = HasVdm.class)
	@Test
	public void Numeric()
	{
		CGenMain.main(new String[] { "-dest", outputFolder,
				getPath("expressions/ExpressionNumeric.vdmrt") });
	}

	@ConditionalIgnore(condition = HasVdm.class)
	@Test
	public void Seq()
	{
		CGenMain.main(new String[] { "-dest", outputFolder,
				getPath("expressions/ExpressionSeq.vdmrt") });
	}

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
	
	@ConditionalIgnore(condition = HasVdm.class)
	@Test
	public void Set()
	{
		CGenMain.main(new String[] {"-dest",outputFolder, getPath("expressions/ExpressionSetForIRTesting.vdmrt") });
	}
	
	@ConditionalIgnore(condition = HasVdm.class)
	@Test
	public void Boolean()
	{
		CGenMain.main(new String[] {"-dest",outputFolder, getPath("expressions/ExpressionBoolean.vdmrt") });
	}
	
//	@ConditionalIgnore(condition = HasVdm.class)
//	@Test
//	public void cases()
//	{
//		CGenMain.main(new String[] {"-dest",outputFolder, "/Users/kel/data/into-cps/vdm2c-exploration/vdm/expressions/ExpressionCases.vdmrt" });
//	}
	
	
}
