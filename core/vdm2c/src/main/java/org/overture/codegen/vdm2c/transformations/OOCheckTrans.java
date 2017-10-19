package org.overture.codegen.vdm2c.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.intf.IAnalysis;
import org.overture.codegen.ir.expressions.AExternalExpIR;
import org.overture.codegen.ir.expressions.AIsOfBaseClassExpIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.analysis.ClassAssocAnalysis;
import org.overture.codegen.vdm2c.utils.CTransUtil;
import org.overture.codegen.vdm2c.utils.IApplyAssistant;

public class OOCheckTrans extends DepthFirstAnalysisCAdaptor implements IApplyAssistant {

    public static final String IS_OF_BASE_CLASS = "isOfBaseClass";
    public static final String C_INT = "int";

    private TransAssistantIR assist;

    public OOCheckTrans(TransAssistantIR assist)
    {
        this.assist = assist;
    }

    @Override
    public TransAssistantIR getAssist() {
        return assist;
    }

    @Override
    public IAnalysis getAnalyzer() {
        return THIS;
    }

    @Override
    public void caseAIsOfBaseClassExpIR(AIsOfBaseClassExpIR node) throws AnalysisException {

        String classId = ClassAssocAnalysis.toClassId(node.getBaseClass());
        SExpIR classExp = consClassExp(classId);

        CTransUtil.rewriteToApply(this, node, IS_OF_BASE_CLASS, node.getExp(), classExp);
    }

    private SExpIR consClassExp(String classId) {
        AExternalExpIR classExp = new AExternalExpIR();
        AExternalTypeIR cInt = new AExternalTypeIR();
        cInt.setName(C_INT);
        classExp.setType(cInt);
        classExp.setTargetLangExp(classId);

        return classExp;
    }
}
