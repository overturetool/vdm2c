package org.overture.codegen.vdm2c;

import java.io.File;

import org.junit.Rule;
import org.overture.test.framework.ConditionalIgnoreMethodRule;

public class BaseGeneratorTest
{
	static final String cexamplesBase = System.getProperty("cexamples.path");
	static final String outputFolder = new File("target/test-cgen/"
			+ CExamplesTest.class.getSimpleName().replace('/', File.separatorChar)).getAbsolutePath();

	@Rule
	public ConditionalIgnoreMethodRule rule = new ConditionalIgnoreMethodRule();

	public static class HasVdm
			implements
			org.overture.test.framework.ConditionalIgnoreMethodRule.IgnoreCondition
	{

		@Override
		public boolean isIgnored()
		{
			return cexamplesBase == null;
		}
	}

	static String getPath(String rpath)
	{
		return new File(cexamplesBase, rpath.replace('/', File.separatorChar)).getAbsolutePath();
	}
}
