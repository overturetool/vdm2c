package org.overture.codegen.vdm2c;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.vdm2c.CMakeUtil.CMakeGenerateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NativeTestBase extends BaseGeneratorTest
{
	private static final String TEST_REPORT = "report.xml";

	private static final String VDMCLIB = "../../c/vdmclib/src/main/";

	final static Logger logger = LoggerFactory.getLogger(NativeTestBase.class);
	
	private static final String FORMATTER = "formatter";

	final static String TEST_OUTPUT = System.getProperty("TEST_OUTPUT");

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
		File[] fileList = root.listFiles(filter);
		
		if (fileList != null)
		{
			for (File f : fileList)
			{
				if (f.isDirectory())
				{
					paths.addAll(Arrays.asList(getFilePaths(f, filter)));

				} else
				{
					paths.add(f.getPath());
				}
			}
		}

		return paths.toArray(new String[] {});
	}

	@Rule
	public TestName name = new TestName();

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
		
		List<String> args = buildArgs(paths);
		
		CGenMain.main(args.toArray(new String[] {}));
	}

	protected List<String> buildArgs(String... paths)
	{
		List<String> args = new Vector<String>(Arrays.asList(new String[] {
				"--quiet", "-dest", root.getAbsolutePath() }));
		args.addAll(Arrays.asList(paths));
		
		if(System.getProperty(FORMATTER)!=null)
		{
			args.add("-formatter");
			args.add(System.getProperty(FORMATTER));
		}
		return args;
	}

	protected void compileAndTest(File... tests) throws IOException,
			InterruptedException, CMakeGenerateException
	{
		CMakeUtil cmakeUtil = new CMakeUtil(new File(VDMCLIB), new File("src/test/resources/CMakeLists.txt"), false);

		copyTestFiles(tests);

		runTests(cmakeUtil);
		
		assertTestsExecuted(tests);
	}

	protected void runTests(CMakeUtil cmakeUtil) throws IOException, InterruptedException, CMakeGenerateException {
		cmakeUtil.createTestProject(name.getMethodName(), root);
		Assert.assertTrue("Failed to run cmake", cmakeUtil.generate(root));
		Assert.assertTrue("Failed to run make and compile", cmakeUtil.make(root));
		Assert.assertTrue("Failed to run tests", cmakeUtil.run(root, name.getMethodName(), TEST_OUTPUT != null));
		Assert.assertTrue("Failed to run make test", cmakeUtil.make(root, "test"));
		runAdditionalTests(cmakeUtil);
	}

	/**
	 * Enables sub-classes to add additional test steps
	 * 
	 * @param cmakeUtil
	 */
	protected void runAdditionalTests(CMakeUtil cmakeUtil)
			throws IOException, InterruptedException, CMakeGenerateException {

	}

	protected void assertTestsExecuted(File... tests) {
		if(tests.length > 0)
		{
			File testReport = new File(root, TEST_REPORT);
			
			Assert.assertTrue("Could not find test report", testReport.exists());
			try
			{
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(testReport);
				XPathFactory xPathfactory = XPathFactory.newInstance();
				XPath xpath = xPathfactory.newXPath();
				XPathExpression expr = xpath.compile("//testsuites[@tests]");
				NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
				
				Assert.assertTrue("Test report does not mention any tests", nl != null && nl.getLength() > 0);

				for (int i = 0; i < nl.getLength(); i++)
				{
					Node currentItem = nl.item(i);
					String nodeValue = currentItem.getAttributes().getNamedItem("tests").getNodeValue();
					Integer testCount = Integer.parseInt(nodeValue);
					Assert.assertTrue("No tests were executed!", testCount != null & testCount > 0);
				}

			} catch (Exception e)
			{
				Assert.fail("Unexpected problem encountered while trying to analyse the test report: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	protected void copyTestFiles(File... tests) throws IOException {
		
		File fixture = getFixtureFile();
		FileUtils.copyFile(fixture, new File(root, fixture.getName()));
		
		for (File file : tests)
		{
			try
			{
				FileUtils.copyFile(file, new File(root, file.getName()));
			}
			catch(IOException e)
			{
				if(e.getMessage().contains("are the same"))
				{
					logger.warn(e.getMessage());
				}
				else
				{
					throw e;
				}
			}
		}
	}

	protected File getFixtureFile()
	{
		return new File("src/test/resources/TestFlowFunctions.h");
	}

	protected String getPath(String rpath)
	{
		return new File(new File(testResourcedVdmRtPath), rpath.replace('/', File.separatorChar)).getAbsolutePath();
	}
}
