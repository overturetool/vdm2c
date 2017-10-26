package org.overture.codegen.vdm2c.transformations;

import org.apache.log4j.Logger;
import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.intf.IAnalysis;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.types.*;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.statements.ALocalVariableDeclarationStmIR;
import org.overture.codegen.vdm2c.utils.CTransUtil;
import org.overture.codegen.vdm2c.utils.IApplyAssistant;

public class IsCheckTrans extends DepthFirstAnalysisCAdaptor implements IApplyAssistant {

    protected Logger log = Logger.getLogger(this.getClass().getName());

    public static final String VDM_IS_NAT = "isNat";
    public static final String VDM_IS_NAT1 = "isNat1";
    public static final String VDM_IS_INT = "isInt";
    public static final String VDM_IS_REAL = "isReal";
    public static final String VDM_IS_BOOL = "isBool";
    public static final String VDM_IS_RAT = "isRat";
    public static final String VDM_IS_CHAR = "isChar";
    public static final String VDM_IS_TOKEN = "isToken";
    public static final String VDM_IS = "is";

    public static final String IS_EXP_ENCODING = "isExpEncoding_";

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
        else
        {
            String enc = IsExpTypeEncoder.encodeType(type);

            if(enc == null)
            {
                log.error("Could not encode type!");
                return;
            }

            AExternalExpIR encodingExp = new AExternalExpIR();
            encodingExp.setTargetLangExp(enc);

            AExternalTypeIR charArr = new AExternalTypeIR();
            charArr.setName("char");

            ABlockStmIR replacement = new ABlockStmIR();

            String varName = assist.getInfo().getTempVarNameGen().nextVarName(IS_EXP_ENCODING);
            AVarDeclIR encodingVar = assist.consDecl(varName + "[]", charArr, encodingExp);
            ALocalVariableDeclarationStmIR declStm = new ALocalVariableDeclarationStmIR();
            declStm.setDecleration(encodingVar);
            replacement.getStatements().add(declStm);
            AIdentifierVarExpIR id = assist.getInfo().getExpAssistant().consIdVar(varName, encodingVar.getType().clone());

            SStmIR stm = assist.getEnclosingStm(node, "is expression");

            CTransUtil.rewriteToApply(this, node, VDM_IS, node.getExp(), id);

            assist.replaceNodeWith(stm, replacement);
            replacement.getStatements().add(stm);
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
