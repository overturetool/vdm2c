package org.overture.codegen.vdm2c.analysis;

import java.util.Arrays;
import java.util.List;

import org.overture.ast.definitions.SClassDefinition;

public class FeatureAnalysisResult
{
	// Features to check for
	private boolean usesSets;
	private boolean usesSeqs;
	private boolean usesMaps;
	
	// Libraries being used
	private boolean usesMathLib;
	private boolean usesCsvLib;
	private boolean usesIoLib;
	private boolean usesVdmUtil;
	private boolean usesVdmUnit;
	
	public static final String MATH_LIB = "MATH";
	public static final String CSV_LIB = "CSV";
	public static final String IO_LIB = "IO";
	public static final String VDMUTIL_LIB = "VDMUtil";
	
	public static final List<String> VDMUNIT_CLASSES = Arrays.asList(new String[] {
			"VDMUnit", "Throwable", "Error", "AssertionFailedError", "Assert",
			"Test", "TestCase", "TestSuite", "TestListener", "TestResult",
			"TestRunner" });
	
	/**
	 * Prevent others from instantiating this class
	 */
	private FeatureAnalysisResult()
	{
	}
	
	public static FeatureAnalysisResult runAnalysis(List<SClassDefinition> ast)
	{
		FeatureAnalysisResult an = new FeatureAnalysisResult();
		an.usesSets = new UsesSetsAnalysis().hasFeature(ast);
		an.usesSeqs = new UsesSeqsAnalysis().hasFeature(ast);
		an.usesMaps = new UsesMapsAnalysis().hasFeature(ast);
		
		for(SClassDefinition c : ast)
		{
			String className = c.getName().getName();
			
			if(className.equals(MATH_LIB))
			{
				an.usesMathLib = true;
			}
			else if(className.equals(CSV_LIB))
			{
				// CSV is a subclass of IO
				an.usesCsvLib = true;
				an.usesIoLib = true;
			}
			else if(className.equals(IO_LIB))
			{
				an.usesIoLib = true;
			}
			else if(className.equals(VDMUTIL_LIB))
			{
				an.usesVdmUtil = true;
			}
			else if(VDMUNIT_CLASSES.contains(className))
			{
				an.usesVdmUnit = true;
			}
		}
		
		return an;
	}
	
	public boolean usesSets()
	{
		return usesSets;
	}
	
	public boolean usesSeqs()
	{
		return usesSeqs;
	}
	
	public boolean usesMaps()
	{
		return usesMaps;
	}
	
	public boolean usesMathLib()
	{
		return usesMathLib;
	}
	
	public boolean usesCsvLib()
	{
		return usesCsvLib;
	}
	
	public boolean usesIoLib()
	{
		return usesIoLib;
	}
	
	public boolean usesVdmUtil()
	{
		return usesVdmUtil;
	}
	
	public boolean usesVdmUnit()
	{
		return usesVdmUnit;
	}
}
