package org.overture.codegen.vdm2c.tests.output.util;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.node.INode;
import org.overture.codegen.utils.GeneratedData;
import org.overture.config.Release;
import org.overture.config.Settings;

abstract public class PpSpecificationTest extends SpecificationTestBase
{
	public PpSpecificationTest(String nameParameter, String inputParameter,
			String resultParameter)
	{
		super(nameParameter, inputParameter, resultParameter);
	}

	@Before
	public void init()
	{
		super.init();
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
	}

	public GeneratedData genCode(List<INode> ast) throws AnalysisException
	{
		List<SClassDefinition> classes = new LinkedList<SClassDefinition>();

		for (INode c : ast)
		{
			if (c instanceof SClassDefinition)
			{
				classes.add((SClassDefinition) c);
			}
			else
			{
				Assert.fail("Expected only classes got " + c);
			}
		}

		return vdmCodGen.generateCFromVdm(classes,new File("target/test-cgen".replace('/', File.separatorChar)));
	}

	abstract protected String getUpdatePropertyString();
}
