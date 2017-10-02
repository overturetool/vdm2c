package org.overture.codegen.vdm2c.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.intf.IAnalysis;
import org.overture.codegen.ir.expressions.AGeneralIsExpIR;
import org.overture.codegen.ir.expressions.ANat1IsExpIR;
import org.overture.codegen.ir.expressions.ANatIsExpIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.utils.CTransUtil;
import org.overture.codegen.vdm2c.utils.IApplyAssistant;

public class IsCheckTrans extends DepthFirstAnalysisCAdaptor implements IApplyAssistant {

    public static final String VDM_IS_NAT = "isNat";
    public static final String VDM_IS_NAT1 = "isNat1";

    private TransAssistantIR assist;

    public IsCheckTrans(TransAssistantIR assist)
    {
        this.assist = assist;
    }

    @Override
    public void caseANatIsExpIR(ANatIsExpIR node) throws AnalysisException {
        CTransUtil.rewriteToApply(this, node, VDM_IS_NAT, node.getExp());
    }

    @Override
    public void caseANat1IsExpIR(ANat1IsExpIR node) throws AnalysisException {
        CTransUtil.rewriteToApply(this, node, VDM_IS_NAT1, node.getExp());
    }

    @Override
    public TransAssistantIR getAssist() {
        return assist;
    }

    @Override
    public IAnalysis getAnalyzer() {
        return THIS;
    }
}
