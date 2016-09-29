package org.overture.codegen.vdm2c.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class RecTypeToClassTypeTrans extends DepthFirstAnalysisCAdaptor
{
	private  TransAssistantIR assist;

	public RecTypeToClassTypeTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseARecordTypeIR(ARecordTypeIR node) throws AnalysisException
	{
		AClassTypeIR classType = new AClassTypeIR();
		classType.setSourceNode(node.getSourceNode());
		classType.setName(node.getName().getName());

		assist.replaceNodeWith(node, classType);
	}
}
