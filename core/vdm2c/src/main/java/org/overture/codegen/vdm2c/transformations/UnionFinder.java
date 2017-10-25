package org.overture.codegen.vdm2c.transformations;


import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.types.AUnionTypeIR;

public class UnionFinder extends DepthFirstAnalysisCAdaptor {

    private boolean hasUnionType;

    private UnionFinder(){

        this.hasUnionType = false;
    }

    public static boolean containsUnion(STypeIR type)
    {
        UnionFinder finder = new UnionFinder();
        try {

            if(type != null)
            {
                type.apply(finder);
            }

        } catch (AnalysisException e)
        {
            // Analysis was stopped
        }

        return finder.hasUnionType;
    }

    @Override
    public void caseAUnionTypeIR(AUnionTypeIR node) throws AnalysisException {

        hasUnionType = true;
        throw new AnalysisException("Union type found!");
    }
}
