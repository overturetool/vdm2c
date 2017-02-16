package org.overture.codegen.vdm2c.analysis;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.ARecordInvariantType;

public class UsesRecordsAnalysis extends FeatureAnalysis
{
	@Override
	public void caseARecordInvariantType(ARecordInvariantType node)
			throws AnalysisException
	{
		foundFeature();
	}
}
