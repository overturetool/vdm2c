package org.overture.codegen.vdm2c.transformations;


import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AGeneralIsExpIR;
import org.overture.codegen.ir.expressions.AOrBoolBinaryExpIR;
import org.overture.codegen.ir.expressions.SVarExpIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.statements.ALocalVariableDeclarationStmIR;

import java.util.List;

public class IsExpUnionTypeTrans extends DepthFirstAnalysisCAdaptor {

    private TransAssistantIR assist;

    public static final String IS_EXP_SUBJECT = "isExpSubject_";

    public IsExpUnionTypeTrans(TransAssistantIR assist)
    {
        this.assist = assist;
    }

    @Override
    public void caseAGeneralIsExpIR(AGeneralIsExpIR node) throws AnalysisException {

        super.caseAGeneralIsExpIR(node);

        STypeIR type = node.getCheckedType();

        List<STypeIR> types = IsExpUnionTypeFinder.findTypes(assist, type);

        if(types.size() > 1)
        {
            if(!(node.getExp() instanceof SVarExpIR))
            {
                String varName = assist.getInfo().getTempVarNameGen().nextVarName(IS_EXP_SUBJECT);
                AVarDeclIR decl = assist.consDecl(varName, node.getExp().getType().clone(), node.getExp().clone());
                ALocalVariableDeclarationStmIR declStm = new ALocalVariableDeclarationStmIR();
                declStm.setDecleration(decl);

                assist.replaceNodeWith(node.getExp(), assist.getInfo().getExpAssistant().consIdVar(varName, decl.getType().clone()));

                SStmIR stm = assist.getEnclosingStm(node, "is expression (" + this.getClass().getSimpleName() + ")");
                ABlockStmIR replacement = new ABlockStmIR();
                assist.replaceNodeWith(stm, replacement);

                replacement.getStatements().add(declStm);
                replacement.getStatements().add(stm);
            }

            // 'type' contains union types

            AOrBoolBinaryExpIR or = new AOrBoolBinaryExpIR();
            or.setType(new ABoolBasicTypeIR());
            or.setLeft(consCheck(node.getExp(), types.get(0)));

            AOrBoolBinaryExpIR next = or;

            // Iterate over all types, except the first and last ones
            for(int i = 1; i < types.size() - 1; i++)
            {
                STypeIR nextType = types.get(i);

                AOrBoolBinaryExpIR tmp = new AOrBoolBinaryExpIR();
                tmp.setType(new ABoolBasicTypeIR());
                tmp.setLeft(consCheck(node.getExp(), nextType));
                next.setRight(tmp);

                next = tmp;
            }

            // Construct the check for the last type
            next.setRight(consCheck(node.getExp(), types.get(types.size() - 1)));

            assist.replaceNodeWith(node, or);
        }
    }

    private SExpIR consCheck(SExpIR exp, STypeIR type)
    {
        AGeneralIsExpIR check = new AGeneralIsExpIR();
        check.setType(new ABoolBasicTypeIR());
        check.setCheckedType(type.clone());
        check.setExp(exp.clone());

        return check;
    }
}
