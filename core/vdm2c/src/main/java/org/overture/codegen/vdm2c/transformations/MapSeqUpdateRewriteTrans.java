package org.overture.codegen.vdm2c.transformations;

import static org.overture.codegen.vdm2c.utils.CTransUtil.newApply;
import static org.overture.codegen.vdm2c.utils.CTransUtil.toStm;

import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.AMapSeqStateDesignator;
import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.statements.AMapSeqUpdateStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class MapSeqUpdateRewriteTrans extends DepthFirstAnalysisCAdaptor
{
	public TransAssistantIR assist;

	public MapSeqUpdateRewriteTrans(TransAssistantIR assist)
	{
		this.assist = assist;
	}

	@Override
	public void caseAMapSeqUpdateStmIR(AMapSeqUpdateStmIR node)
			throws AnalysisException
	{
		super.caseAMapSeqUpdateStmIR(node);
		AAssignmentStm assign = (AAssignmentStm) node.getSourceNode().getVdmNode();

		if (assign.getTarget() instanceof AMapSeqStateDesignator)
		{
			AMapSeqStateDesignator designator = (AMapSeqStateDesignator) assign.getTarget();

			String methodName = null;
			if (designator.getMapType() != null)
			{
				// map update
				methodName = "vdmMapUpdate";
			} else if (designator.getSeqType() != null)
			{
				// seq update
				methodName = "vdmSeqUpdate";
			}
			AApplyExpIR newApply = newApply(methodName, node.getCol(), node.getIndex(), node.getValue());
			newApply.setSourceNode(node.getSourceNode());
			assist.replaceNodeWith(node, toStm(newApply));
		}

	}

}
