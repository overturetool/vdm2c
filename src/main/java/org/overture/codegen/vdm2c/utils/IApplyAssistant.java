package org.overture.codegen.vdm2c.utils;

import org.overture.codegen.cgast.analysis.intf.IAnalysis;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public interface IApplyAssistant extends IAnalysis
{
	public TransAssistantCG getAssist();
	public IAnalysis getAnalyzer();
}
