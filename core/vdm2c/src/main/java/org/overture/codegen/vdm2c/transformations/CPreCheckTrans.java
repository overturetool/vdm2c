package org.overture.codegen.vdm2c.transformations;

import org.apache.log4j.Logger;
import org.overture.cgc.extast.analysis.DepthFirstAnalysisCAdaptor;
import org.overture.codegen.ir.SDeclIR;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.AAndBoolBinaryExpIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AExternalExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ACallObjectStmIR;
import org.overture.codegen.ir.statements.APlainCallStmIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.AUnknownTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;
import org.overture.codegen.vdm2c.extast.statements.ALocalVariableDeclarationStmIR;
import org.overture.codegen.vdm2c.utils.CTransUtil;

import java.util.LinkedList;
import java.util.List;

public class CPreCheckTrans extends DepthFirstAnalysisCAdaptor {

    public static final String C_ASSERT_NAME = "assert";
    public static final String PRE_RES_PREFIX = "preRes_";

    private Logger log = Logger.getLogger(this.getClass().getName());

    private TransAssistantIR transAssistant;

    public CPreCheckTrans(TransAssistantIR assist)
    {
        this.transAssistant = assist;
    }

    @Override
    public void caseAMethodDeclIR(AMethodDeclIR node) throws AnalysisException
    {
        if (!transAssistant.getInfo().getSettings().generatePreCondChecks())
        {
            return;
        }

        SDeclIR preCond = node.getPreCond();

        if (preCond == null)
        {
            return;
        }

        if (!(preCond instanceof AMethodDeclIR))
        {
            log.error("Expected pre condition to be a method declaration at this point. Got: "
                    + preCond);
            return;
        }

        AMethodDeclIR preCondMethod = (AMethodDeclIR) preCond;

        SClassDeclIR encClass = node.getAncestor(SClassDeclIR.class);

        if(encClass == null)
        {
            log.error("Could not find enclosing class for method " + node);
            return;
        }

        List<SExpIR> args = new LinkedList<>();

        LinkedList<AFormalParamLocalParamIR> params = node.getFormalParams();

        for (AFormalParamLocalParamIR p : params)
        {
            SPatternIR paramPattern = p.getPattern();

            if (!(paramPattern instanceof AIdentifierPatternIR))
            {
                log.error("Expected parameter pattern to be an identifier pattern at this point. Got: "
                        + paramPattern);
                return;
            }

            AIdentifierPatternIR paramId = (AIdentifierPatternIR) paramPattern;

            if(paramId.getName().equals("this")){
                continue;
            }

            AIdentifierVarExpIR paramArg = new AIdentifierVarExpIR();
            paramArg.setType(p.getType().clone());
            paramArg.setIsLocal(true);
            paramArg.setIsLambda(false);
            paramArg.setName(paramId.getName());

            args.add(paramArg);
        }

        AMacroApplyExpIR preCondCall = CallRewriteTrans.createLocalPtrApply(preCondMethod, encClass.getName(), args);

        if (preCondCall == null)
        {
            return;
        }

        SStmIR body = node.getBody();

        String name = transAssistant.getInfo().getTempVarNameGen().nextVarName(PRE_RES_PREFIX);


        AVarDeclIR var = transAssistant.consDecl(name, new ABoolBasicTypeIR(), preCondCall);
        ALocalVariableDeclarationStmIR declStm = new ALocalVariableDeclarationStmIR();
        declStm.setDecleration(var);

        AExternalExpIR assertionArg = new AExternalExpIR();
        assertionArg.setTargetLangExp(String.format("toBool(%s) && \"Precondition failure: %s\"", name, preCondMethod.getName()));

        // Example:
        // TVP preRes_1 = CALL_FUNC_PTR(A, A, this, CLASS_A__Z17pre_opEV);
        // assert(toBool(preRes_1) && "Precondition failure: _Z17pre_opEV");
        // vdmFree(preRes_1);

        AMacroApplyExpIR assertion = CTransUtil.newMacroApply(C_ASSERT_NAME);
        assertion.getArgs().add(assertionArg);

        ABlockStmIR newBody = new ABlockStmIR();
        newBody.getStatements().add(declStm);
        newBody.getStatements().add(CTransUtil.exp2Stm(assertion));
        newBody.getStatements().add(CTransUtil.exp2Stm(ValueSemantics.free(name, null)));
        newBody.getStatements().add(body.clone());

        transAssistant.replaceNodeWith(body, newBody);
    }

    public AApplyExpIR consConditionalCall(AMethodDeclIR node,
                                           AMethodDeclIR predMethod)
    {
        AIdentifierVarExpIR condVar = new AIdentifierVarExpIR();
        condVar.setType(predMethod.getMethodType().clone());
        condVar.setName(predMethod.getName());
        condVar.setIsLambda(false);
        condVar.setIsLocal(true);

        AApplyExpIR condCall = new AApplyExpIR();
        condCall.setType(new ABoolBasicTypeIR());
        condCall.setRoot(condVar);

        List<AFormalParamLocalParamIR> params = node.getFormalParams();

        for (AFormalParamLocalParamIR p : params) {
            SPatternIR paramPattern = p.getPattern();

            if (!(paramPattern instanceof AIdentifierPatternIR)) {
                log.error("Expected parameter pattern to be an identifier pattern at this point. Got: "
                        + paramPattern);
                return null;
            }

            AIdentifierPatternIR paramId = (AIdentifierPatternIR) paramPattern;

            AIdentifierVarExpIR paramArg = new AIdentifierVarExpIR();
            paramArg.setType(p.getType().clone());
            paramArg.setIsLocal(true);
            paramArg.setIsLambda(false);
            paramArg.setName(paramId.getName());

            condCall.getArgs().add(paramArg);
        }

        return condCall;
    }
}
