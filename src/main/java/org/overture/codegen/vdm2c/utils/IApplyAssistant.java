package org.overture.codegen.vdm2c.utils;

import org.overture.codegen.ir.analysis.intf.IAnalysis;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public interface IApplyAssistant extends IAnalysis
{
	public TransAssistantIR getAssist();
	public IAnalysis getAnalyzer();
}
