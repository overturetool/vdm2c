package org.overture.codegen.vdm2c.tests.output;

import java.io.File;
import java.util.Collection;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.vdm2c.tests.output.util.OutputTestUtil;
import org.overture.codegen.vdm2c.tests.output.util.PpSpecificationTest;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.core.tests.PathsProvider;

@RunWith(Parameterized.class)
public class RtOutputTest extends PpSpecificationTest
{
	public static final String ROOT = "src/test/resources/rt".replace('/', File.separatorChar);

	public RtOutputTest(String nameParameter, String inputParameter,
			String resultParameter)
	{
		super(nameParameter, inputParameter, resultParameter);
	}
	
	@Before
	public void init()
	{
		super.init();
		
		Settings.release = Release.VDM_10;
		Settings.dialect = Dialect.VDM_RT;
	}
	
	@Override
	public IRSettings getIrSettings()
	{
		IRSettings irSettings = new IRSettings();
		irSettings.setGenerateConc(true);

		return irSettings;
	}

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		return PathsProvider.computePaths(ROOT);
	}

	@Override
	protected String getUpdatePropertyString()
	{
		return OutputTestUtil.UPDATE_PROPERTY_PREFIX + "rt";
	}
}
