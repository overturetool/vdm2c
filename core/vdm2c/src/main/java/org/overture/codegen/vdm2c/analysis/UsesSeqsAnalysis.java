package org.overture.codegen.vdm2c.analysis;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;

public class UsesSeqsAnalysis extends FeatureAnalysis
{	
	@Override
	public void caseASeq1SeqType(ASeq1SeqType node) throws AnalysisException
	{
		foundFeature();
	}
	
	@Override
	public void caseASeqSeqType(ASeqSeqType node) throws AnalysisException
	{
		foundFeature();
	}
}
