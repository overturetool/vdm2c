package org.overture.codegen.vdm2c.analysis;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.AInMapMapType;
import org.overture.ast.types.AMapMapType;

public class UsesMapsAnalysis extends FeatureAnalysis
{
	@Override
	public void caseAInMapMapType(AInMapMapType node) throws AnalysisException
	{
		foundFeature();
	}
	
	@Override
	public void caseAMapMapType(AMapMapType node) throws AnalysisException
	{
		foundFeature();
	}
}
