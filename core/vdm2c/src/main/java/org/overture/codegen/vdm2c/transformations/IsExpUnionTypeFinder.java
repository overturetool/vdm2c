package org.overture.codegen.vdm2c.transformations;


import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.types.AUnionTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

import java.util.LinkedList;
import java.util.List;

public class IsExpUnionTypeFinder extends DepthFirstAnalysisCAdaptor {

    private TransAssistantIR assist;
    private STypeIR root;
    private List<STypeIR> types;

    private IsExpUnionTypeFinder(TransAssistantIR assist)
    {
        this.assist = assist;
        this.root = null;
        this.types = new LinkedList<>();
    }

    public static List<STypeIR> findTypes(TransAssistantIR assist, STypeIR type) throws AnalysisException {
        IsExpUnionTypeFinder finder = new IsExpUnionTypeFinder(assist);

        if(type != null)
        {
            try {
                type.apply(finder);
            } catch(Exception e)
            {
                // Analysis was stopped
            }
        }

        List<STypeIR> result = new LinkedList<>();
        List<STypeIR> nextPass = finder.types;

        while(!nextPass.isEmpty()) {

            STypeIR t = nextPass.remove(0);

            if (UnionFinder.containsUnion(t)) {

                IsExpUnionTypeFinder f = new IsExpUnionTypeFinder(assist);

                try {

                    t.apply(f);
                }
                catch (AnalysisException e)
                {
                    // Analysis was stopped
                }

                nextPass.addAll(0, f.types);

            } else {

                result.add(t);
            }
        }
        return result;
    }

    @Override
    public void defaultInSTypeIR(STypeIR node) throws AnalysisException {

        if(root == null)
        {
            // We're visiting the root

            if(!UnionFinder.containsUnion(node))
            {
                types.add(node.clone());
                // If the type contains no union types we're done
                return;
            }

            root = node;
        }

        super.defaultSTypeIR(node);
    }

    @Override
    public void caseAUnionTypeIR(AUnionTypeIR node) throws AnalysisException {

        ensureRoot(node);

        for(STypeIR t : new LinkedList<STypeIR>(node.getTypes()))
        {
            STypeIR clone = t.clone();

            if(node.parent() != null)
            {
                assist.replaceNodeWith(node, clone);
                types.add(root.clone());
                assist.replaceNodeWith(clone, node);
            }
            else
            {
                types.add(t.clone());
            }
        }

        // Only a single union type can be processed per pass
        throw new AnalysisException("Union type processed");
    }

    private void ensureRoot(AUnionTypeIR node) {
        if(root == null)
        {
            root = node;
        }
    }
}
