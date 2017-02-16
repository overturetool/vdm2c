package org.overture.codegen.vdm2c;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.vdm2c.analysis.FeatureAnalysisResult;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class FeatureAnalysisTest
{
	@BeforeClass
	public static void setUp()
	{
		Settings.dialect = Dialect.VDM_RT;
		Settings.release = Release.VDM_10;
	}

	/*
	 * Sets
	 */

	@Test
	public void usesSetEnum()
	{
		List<SClassDefinition> ast = buildAst("class A values s = {} end A");
		FeatureAnalysisResult anRes = FeatureAnalysisResult.runAnalysis(ast);
		Assert.assertTrue(anRes.usesSets());
		Assert.assertFalse(anRes.usesSeqs());
		Assert.assertFalse(anRes.usesMaps());
		Assert.assertFalse(anRes.usesPatterns());
		assertNoLibs(anRes);
	}

	@Test
	public void usesSet1Type()
	{
		List<SClassDefinition> ast = buildAst("class A instance variables xs : set1 of nat; end A");
		FeatureAnalysisResult anRes = FeatureAnalysisResult.runAnalysis(ast);
		Assert.assertTrue(anRes.usesSets());
		Assert.assertFalse(anRes.usesSeqs());
		Assert.assertFalse(anRes.usesMaps());
		Assert.assertFalse(anRes.usesPatterns());
		assertNoLibs(anRes);
	}

	@Test
	public void noSets()
	{
		List<SClassDefinition> ast = buildAst("class A end A");
		FeatureAnalysisResult anRes = FeatureAnalysisResult.runAnalysis(ast);
		Assert.assertFalse(anRes.usesSets());
		Assert.assertFalse(anRes.usesSeqs());
		Assert.assertFalse(anRes.usesMaps());
		Assert.assertFalse(anRes.usesPatterns());
		assertNoLibs(anRes);
	}

	/*
	 * Sequences
	 */

	@Test
	public void usesSeqEnum()
	{
		List<SClassDefinition> ast = buildAst("class A values s = [] end A");
		FeatureAnalysisResult anRes = FeatureAnalysisResult.runAnalysis(ast);
		Assert.assertFalse(anRes.usesSets());
		Assert.assertTrue(anRes.usesSeqs());
		Assert.assertFalse(anRes.usesMaps());
		Assert.assertFalse(anRes.usesPatterns());
		assertNoLibs(anRes);
	}

	@Test
	public void usesSeq1Type()
	{
		List<SClassDefinition> ast = buildAst("class A instance variables xs : seq1 of nat; end A");
		FeatureAnalysisResult anRes = FeatureAnalysisResult.runAnalysis(ast);
		Assert.assertFalse(anRes.usesSets());
		Assert.assertTrue(anRes.usesSeqs());
		Assert.assertFalse(anRes.usesMaps());
		Assert.assertFalse(anRes.usesPatterns());
		assertNoLibs(anRes);
	}

	@Test
	public void noSeqs()
	{
		List<SClassDefinition> ast = buildAst("class A end A");
		FeatureAnalysisResult anRes = FeatureAnalysisResult.runAnalysis(ast);
		Assert.assertFalse(anRes.usesSets());
		Assert.assertFalse(anRes.usesSeqs());
		Assert.assertFalse(anRes.usesMaps());
		Assert.assertFalse(anRes.usesPatterns());
		assertNoLibs(anRes);
	}

	/*
	 * Maps
	 */

	@Test
	public void usesMapEnum()
	{
		List<SClassDefinition> ast = buildAst("class A values s = {|->} end A");
		FeatureAnalysisResult anRes = FeatureAnalysisResult.runAnalysis(ast);
		Assert.assertFalse(anRes.usesSets());
		Assert.assertFalse(anRes.usesSeqs());
		Assert.assertTrue(anRes.usesMaps());
		Assert.assertFalse(anRes.usesPatterns());
		assertNoLibs(anRes);
	}

	@Test
	public void usesInjectiveMaps()
	{
		List<SClassDefinition> ast = buildAst("class A values xs : [inmap nat to nat] = nil; end A");
		FeatureAnalysisResult anRes = FeatureAnalysisResult.runAnalysis(ast);
		Assert.assertFalse(anRes.usesSets());
		Assert.assertFalse(anRes.usesSeqs());
		Assert.assertTrue(anRes.usesMaps());
		Assert.assertFalse(anRes.usesPatterns());
		assertNoLibs(anRes);
	}

	@Test
	public void noMaps()
	{
		List<SClassDefinition> ast = buildAst("class A end A");
		FeatureAnalysisResult anRes = FeatureAnalysisResult.runAnalysis(ast);
		Assert.assertFalse(anRes.usesSets());
		Assert.assertFalse(anRes.usesSeqs());
		Assert.assertFalse(anRes.usesMaps());
		Assert.assertFalse(anRes.usesPatterns());
		assertNoLibs(anRes);
	}

	/*
	 * Patterns
	 */
	@Test
	public void noPatterns()
	{
		// From the perspective of VDM2C we don't consider the identifier pattern a pattern
		List<SClassDefinition> ast = buildAst("class A values a = 1 end A");
		FeatureAnalysisResult anRes = FeatureAnalysisResult.runAnalysis(ast);
		Assert.assertFalse(anRes.usesSets());
		Assert.assertFalse(anRes.usesSeqs());
		Assert.assertFalse(anRes.usesMaps());
		Assert.assertFalse(anRes.usesPatterns());
		assertNoLibs(anRes);
	}
	
	@Test
	public void ignorePattern()
	{
		List<SClassDefinition> ast = buildAst("class A values - = 1 end A");
		FeatureAnalysisResult anRes = FeatureAnalysisResult.runAnalysis(ast);
		Assert.assertFalse(anRes.usesSets());
		Assert.assertFalse(anRes.usesSeqs());
		Assert.assertFalse(anRes.usesMaps());
		Assert.assertTrue(anRes.usesPatterns());
		assertNoLibs(anRes);
	}
	
	@Test
	public void usesTuplePattern()
	{
		List<SClassDefinition> ast = buildAst("class A values mk_(a,b) = mk_(1,2) end A");
		FeatureAnalysisResult anRes = FeatureAnalysisResult.runAnalysis(ast);
		Assert.assertFalse(anRes.usesSets());
		Assert.assertFalse(anRes.usesSeqs());
		Assert.assertFalse(anRes.usesMaps());
		Assert.assertTrue(anRes.usesPatterns());
		assertNoLibs(anRes);
	}
	
	/*
	 * Libraries
	 */

	@Test
	public void usesLibs()
	{
		// CSV is a subclass of IO
		List<SClassDefinition> ast = buildAst("class CSV end CSV"
				+ " class MATH end MATH " + "class VDMUnit end VDMUnit "
				+ "class VDMUtil end VDMUtil");
		FeatureAnalysisResult anRes = FeatureAnalysisResult.runAnalysis(ast);
		Assert.assertFalse(anRes.usesSets());
		Assert.assertFalse(anRes.usesSeqs());
		Assert.assertFalse(anRes.usesMaps());

		Assert.assertTrue(anRes.usesCsvLib());
		Assert.assertTrue(anRes.usesIoLib());
		Assert.assertTrue(anRes.usesMathLib());
		Assert.assertTrue(anRes.usesVdmUnit());
		Assert.assertTrue(anRes.usesVdmUtil());
	}

	/*
	 * Utility methods
	 */

	private void assertNoLibs(FeatureAnalysisResult anRes)
	{
		Assert.assertFalse(anRes.usesCsvLib());
		Assert.assertFalse(anRes.usesIoLib());
		Assert.assertFalse(anRes.usesMathLib());
		Assert.assertFalse(anRes.usesVdmUnit());
		Assert.assertFalse(anRes.usesVdmUtil());
	}

	private List<SClassDefinition> buildAst(String contents)
	{
		TypeCheckResult<List<SClassDefinition>> tcRes = TypeCheckerUtil.typeCheckPp(contents);

		Assert.assertTrue("Could not type check model", tcRes != null
				&& tcRes.parserResult.errors.isEmpty()
				&& tcRes.errors.isEmpty());

		return tcRes.result;
	}
}
