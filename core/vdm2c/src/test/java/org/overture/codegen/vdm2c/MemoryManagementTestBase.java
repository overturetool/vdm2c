package org.overture.codegen.vdm2c;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.junit.Assert;
import org.overture.codegen.vdm2c.CMakeUtil.CMakeGenerateException;
import org.w3c.dom.Document;

public class MemoryManagementTestBase extends NativeTestBase {

	public static final String MEMORY_REPORT_NAME = "valgrind.xml";

	public MemoryManagementTestBase() {
		super();
	}

	@Override
	protected void runAdditionalTests(CMakeUtil cmakeUtil) throws IOException, InterruptedException, CMakeGenerateException {
	
		// Use valgrind to generate a memory analysis report. See CMakeList.txt
		// for details.
		ProcessBuilder pb = new ProcessBuilder("ctest", "-T", "memcheck");
		pb.directory(root);
	
		cmakeUtil.runProcess(pb, cmakeUtil.isVerbose());
	
		File memReport = new File(root, MEMORY_REPORT_NAME);
	
		Assert.assertTrue("Could not find memory analysis report", memReport.exists());
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
	
			builder = factory.newDocumentBuilder();
	
			Document doc = builder.parse(memReport);
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("//error//kind/text()='Leak_DefinitelyLost'");
	
			Object res = expr.evaluate(doc, XPathConstants.BOOLEAN);
	
			Assert.assertTrue("Expected boolean XPath result but got: " + res, res instanceof Boolean);
	
			Boolean leaks = (Boolean) res;
	
			Assert.assertFalse("The code definitely leaks memory. See the full memory analysis report for details: "
					+ memReport.getAbsolutePath(), leaks);
	
		} catch (Exception e) {
	
			e.printStackTrace();
			Assert.fail("Problems analysing memory report: " + e.getMessage());
		}
	}

}