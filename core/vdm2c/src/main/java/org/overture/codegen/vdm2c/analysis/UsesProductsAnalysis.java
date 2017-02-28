package org.overture.codegen.vdm2c.analysis;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.AProductType;

public class UsesProductsAnalysis extends FeatureAnalysis
{
	@Override
	public void caseAProductType(AProductType node) throws AnalysisException
	{
		foundFeature();
	}
}
