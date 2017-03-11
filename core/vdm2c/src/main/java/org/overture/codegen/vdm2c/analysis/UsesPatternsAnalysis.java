package org.overture.codegen.vdm2c.analysis;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.PPattern;

/**
 * Checks for use of patterns (except for identifier patterns)
 * 
 * @author peter
 *
 */
public class UsesPatternsAnalysis extends FeatureAnalysis
{
	@Override
	public void defaultInPPattern(PPattern node) throws AnalysisException
	{
		if(!(node instanceof AIdentifierPattern))
		{
			foundFeature();
		}
	}
}
