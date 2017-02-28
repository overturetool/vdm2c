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
	private boolean usesPatterns;
	private boolean usesProducts;
	private boolean usesRecords;
	
	// Whether the code-generator uses garbage collection
	private boolean usesGarbageCollection;
	
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
	private FeatureAnalysisResult(boolean usesGarbageCollection)
	{
		this.usesGarbageCollection = usesGarbageCollection;
	}
	
	public static FeatureAnalysisResult runAnalysis(List<SClassDefinition> ast, boolean usesGarbageCollection)
	{
		FeatureAnalysisResult an = new FeatureAnalysisResult(usesGarbageCollection);
		an.usesSets = new UsesSetsAnalysis().hasFeature(ast);
		an.usesSeqs = new UsesSeqsAnalysis().hasFeature(ast);
		an.usesMaps = new UsesMapsAnalysis().hasFeature(ast);
		an.usesPatterns = new UsesPatternsAnalysis().hasFeature(ast);
		an.usesProducts = new UsesProductsAnalysis().hasFeature(ast);
		an.usesRecords = new UsesRecordsAnalysis().hasFeature(ast);
		
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
	
	public boolean usesPatterns()
	{
		return usesPatterns;
	}
	
	public boolean usesProducts()
	{
		return usesProducts;
	}
	
	public boolean usesRecords()
	{
		return usesRecords;
	}
	
	public boolean usesGarbageCollection()
	{
		return usesGarbageCollection;
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
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		appendDef(sb, usesMaps || usesSeqs || usesSets, "#define NO_SETS");
		appendDef(sb, usesMaps || usesSeqs, "#define NO_SEQS");
		appendDef(sb, usesMaps, "#define NO_MAPS");
		appendDef(sb, usesPatterns, "#define NO_PATTERNS");
		appendDef(sb, usesProducts, "#define NO_PRODUCTS");
		appendDef(sb, usesRecords, "#define NO_RECORDS");
		appendDef(sb, usesGarbageCollection, "#define NO_GC");
		appendDef(sb, usesMathLib, "#define NO_MATH");
		appendDef(sb, usesCsvLib, "#define NO_CSV");
		appendDef(sb, usesIoLib, "#define NO_IO");
		appendDef(sb, usesVdmUtil, "#define NO_VDMUTIL");
		appendDef(sb, usesVdmUnit, "#define NO_VDMUNIT");
		
		return sb.toString();
	}

	private void appendDef(StringBuilder sb, boolean usesFeature, String defStr)
	{
		if(!usesFeature)
		{
			sb.append(defStr);
			sb.append('\n');
		}
	}
}
