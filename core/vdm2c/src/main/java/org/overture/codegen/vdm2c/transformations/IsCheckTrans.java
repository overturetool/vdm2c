package org.overture.codegen.vdm2c.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.intf.IAnalysis;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.ir.types.*;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.utils.CTransUtil;
import org.overture.codegen.vdm2c.utils.IApplyAssistant;

public class IsCheckTrans extends DepthFirstAnalysisCAdaptor implements IApplyAssistant {

    public static final String VDM_IS_NAT = "isNat";
    public static final String VDM_IS_NAT1 = "isNat1";
    public static final String VDM_IS_INT = "isInt";
    public static final String VDM_IS_REAL = "isReal";
    public static final String VDM_IS_BOOL = "isBool";
    public static final String VDM_IS_RAT = "isRat";
    public static final String VDM_IS_CHAR = "isChar";
    public static final String VDM_IS_TOKEN = "isToken";

    private TransAssistantIR assist;

    public IsCheckTrans(TransAssistantIR assist)
    {
        this.assist = assist;
    }

    @Override
    public void caseAGeneralIsExpIR(AGeneralIsExpIR node) throws AnalysisException {

        STypeIR type = node.getCheckedType();

        if(type instanceof ANatNumericBasicTypeIR)
        {
            CTransUtil.rewriteToApply(this, node, VDM_IS_NAT, node.getExp());
        }
        else if(type instanceof ANat1NumericBasicTypeIR)
        {
            CTransUtil.rewriteToApply(this, node, VDM_IS_NAT1, node.getExp());
        }
        else if(type instanceof AIntNumericBasicTypeIR)
        {
            CTransUtil.rewriteToApply(this, node, VDM_IS_INT, node.getExp());
        }
        else if(type instanceof ARealNumericBasicTypeIR)
        {
            CTransUtil.rewriteToApply(this, node, VDM_IS_REAL, node.getExp());
        }
        else if(type instanceof ABoolBasicTypeIR)
        {
            CTransUtil.rewriteToApply(this, node, VDM_IS_BOOL, node.getExp());
        }
        else if(type instanceof ARatNumericBasicTypeIR)
        {
            CTransUtil.rewriteToApply(this, node, VDM_IS_RAT, node.getExp());
        }
        else if(type instanceof ACharBasicTypeIR)
        {
            CTransUtil.rewriteToApply(this, node, VDM_IS_CHAR, node.getExp());
        }
        else if(type instanceof ATokenBasicTypeIR)
        {
            CTransUtil.rewriteToApply(this, node, VDM_IS_TOKEN, node.getExp());
        }
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
