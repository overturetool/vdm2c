package org.overture.codegen.vdm2c.analysis;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.AIsExp;

public class UsesIsExpAnalysis extends FeatureAnalysis {

    @Override
    public void caseAIsExp(AIsExp node) throws AnalysisException {

        foundFeature();
    }
}
