package org.overture.codegen.vdm2c.transformations;

import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AVarDeclIR;

public class EnsureValueSemanticsTrans extends DepthFirstAnalysisCAdaptor {

    @Override
    public void caseAVarDeclIR(AVarDeclIR node) throws AnalysisException {
        super.caseAVarDeclIR(node);
        if (node.getTag() != ValueSemantics.NO_CLONE_TAG) {
            node.setExp(ValueSemantics.clone(node.getExp()));
        }
    }
}