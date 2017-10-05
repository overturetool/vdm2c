package org.overture.codegen.vdm2c.analysis;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.codegen.assistant.DeclAssistantIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.CFormat;

import java.util.*;

public class ClassAssocAnalysis extends DepthFirstAnalysisAdaptor {

    private DeclAssistantIR declAssist;

    private List<String> childParentAssocs;

    public ClassAssocAnalysis(DeclAssistantIR declAssist)

    {
        this.declAssist = declAssist;
        this.childParentAssocs = new LinkedList<>();
    }

    public static String runAnalysis(List<SClassDefinition> ast, DeclAssistantIR declAssist) throws AnalysisException {
        ClassAssocAnalysis an  = new ClassAssocAnalysis(declAssist);

        for(SClassDefinition c : ast)
        {
            c.apply(an);
        }

        return an.toString();
    }

    @Override
    public void caseAClassClassDefinition(AClassClassDefinition node) throws AnalysisException {

        for(SClassDefinition s : node.getSuperDefs())
        {
            if(!declAssist.isLibrary(s)) {
                childParentAssocs.add(node.getName().getName());
                childParentAssocs.add(s.getName().getName());
            }
        }
    }

    public static String toClassId(String name)
    {
        return String.format("CLASS_ID_%s_ID",name);
    }

    public static String toInclude(String name)
    {
        return String.format("#include \"%s.h\"", name);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append(CFormat.getGeneratedFileComment());

        if(!childParentAssocs.isEmpty()) {
            List<String> uniqueNames = new LinkedList(new HashSet<>(childParentAssocs));
            Collections.sort(uniqueNames);

            for (String u : uniqueNames) {
                sb.append(toInclude(u));
                sb.append('\n');
            }

            sb.append("\n");
        }

        sb.append("int assoc[] = {");

        String sep = "";
        for(String n : childParentAssocs)
        {
            sb.append(sep + toClassId(n));
            sep = ", ";
        }

        sb.append("};");

        return sb.toString();
    }
}
