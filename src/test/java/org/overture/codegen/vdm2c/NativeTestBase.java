package org.overture.codegen.vdm2c;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.vdm2c.CMakeUtil.CMakeGenerateException;

public class NativeTestBase extends BaseGeneratorTest
{
	final static String VDM_LIB_PATH = System.getProperty("VDM_LIB_PATH");

	final static String testResourcedVdmRtPath = "src/test/resources/vdmrt/".replace('/', File.separatorChar);

	final static FilenameFilter vdmRtFileFilter = new FilenameFilter()
	{

		@Override
		public boolean accept(File dir, String name)
		{
			return new File(dir, name).isDirectory()
					|| Dialect.VDM_RT.getFilter().accept(dir, name);
		}
	};

	File root = null;

	protected File getTestCppFile(String pathRelativeToNative)
	{
		return new File(("src/test/resources/native/" + pathRelativeToNative).replace('/', File.separatorChar));
	}

	protected String[] getFilePaths(File root, FilenameFilter filter)
	{
		List<String> paths = new Vector<String>();
		for (File f : root.listFiles(filter))
		{
			if (f.isDirectory())
			{
				paths.addAll(Arrays.asList(getFilePaths(f, filter)));

			} else
			{
				paths.add(f.getPath());
			}
		}

		return paths.toArray(new String[] {});
	}

	@Rule
	public TestName name = new TestName();

	public static class HasVdmLib
			implements
			org.overture.test.framework.ConditionalIgnoreMethodRule.IgnoreCondition
	{

		@Override
		public boolean isIgnored()
		{
			return VDM_LIB_PATH == null;
		}
	}

	@Before
	public void initRoot()
	{
		String outputFolder = new File("target/test-cgen/"
				+ getClass().getSimpleName() + "/"
				+ name.getMethodName().replace('/', File.separatorChar)).getAbsolutePath();
		root = new File(outputFolder);
	}

	protected void generate(String... paths)
	{
		for (String string : paths)
		{
			if (!new File(string).exists())
			{
				Assert.fail("Input path does not exist: " + string);
				return;
			}
			
		}
		List<String> args = new Vector<String>(Arrays.asList(new String[] {
				"-dest", root.getAbsolutePath() }));
		args.addAll(Arrays.asList(paths));
		CGenMain.main(args.toArray(new String[] {}));
	}

	protected void compileAndTest(File... tests) throws IOException,
			InterruptedException, CMakeGenerateException
	{
		CMakeUtil cmakeUtil = new CMakeUtil(new File(VDM_LIB_PATH), new File("src/test/resources/CMakeLists.txt"), false);

		for (File file : tests)
		{
			FileUtils.copyFile(file, new File(root, file.getName()));
		}

		cmakeUtil.createTestProject(name.getMethodName(), root);
		Assert.assertTrue("Failed to run cmake", cmakeUtil.generate(root));
		Assert.assertTrue("Failed to run make and compile", cmakeUtil.make(root));
		Assert.assertTrue("Failed to run tests", cmakeUtil.run(root, name.getMethodName(), true));
		Assert.assertTrue("Failed to run make test", cmakeUtil.make(root, "test"));

	}

	protected String getPath(String rpath)
	{
		return new File(new File(testResourcedVdmRtPath), rpath.replace('/', File.separatorChar)).getAbsolutePath();
	}
}
