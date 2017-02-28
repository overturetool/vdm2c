package org.overture.codegen.vdm2c.analysis;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.ASet1SetType;
import org.overture.ast.types.ASetSetType;

public class UsesSetsAnalysis extends FeatureAnalysis
{
	@Override
	public void caseASetSetType(ASetSetType arg0) throws AnalysisException
	{
		foundFeature();
	}
	
	@Override
	public void caseASet1SetType(ASet1SetType arg0) throws AnalysisException
	{
		foundFeature();
	}
}
