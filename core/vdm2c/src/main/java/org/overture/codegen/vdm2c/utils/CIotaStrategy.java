package org.overture.codegen.vdm2c.utils;

import org.overture.codegen.ir.*;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.expressions.AExternalExpIR;
import org.overture.codegen.ir.expressions.AGreaterNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.APlusNumericBinaryExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.AIfStmIR;
import org.overture.codegen.ir.statements.AIncrementStmIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.ANatNumericBasicTypeIR;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.iota.IotaStrategy;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.trans.quantifier.CounterData;
import org.overture.codegen.vdm2c.extast.expressions.AMacroApplyExpIR;

import java.util.List;

public class CIotaStrategy extends IotaStrategy {

    public CIotaStrategy(TransAssistantIR transformationAssistant, SExpIR predicate, String resultVarName, String counterName, ILanguageIterator langIterator, ITempVarGen tempGen, IterationVarPrefixes iteVarPrefixes, CounterData counterData) {
        super(transformationAssistant, predicate, resultVarName, counterName, langIterator, tempGen, iteVarPrefixes, counterData);
    }

    @Override
    public SExpIR getForLoopCond(AIdentifierVarExpIR setVar, List<SPatternIR> patterns, SPatternIR pattern) throws AnalysisException {

        SExpIR cond = super.getForLoopCond(setVar, patterns, pattern);
        return CTransUtil.newApply("toBool", cond);
    }

    @Override
    public List<SStmIR> getForLoopStms(AIdentifierVarExpIR setVar,
                                       List<SPatternIR> patterns, SPatternIR pattern)
    {
        if(lastBind)
        {
            AIdentifierVarExpIR col = new AIdentifierVarExpIR();
            col.setType(new AIntNumericBasicTypeIR());
            col.setIsLambda(false);
            col.setIsLocal(true);
            col.setName(counterName);

            // PASTE
            AAssignToExpStmIR incByOne = new AAssignToExpStmIR();
            incByOne.setTarget(col);

            APlusNumericBinaryExpIR plusOne = new APlusNumericBinaryExpIR();
            plusOne.setType(new ABoolBasicTypeIR());
            plusOne.setLeft(col.clone());
            plusOne.setRight(transAssist.getInfo().getExpAssistant().consIntLiteral(1));

            incByOne.setExp(plusOne);

            // END PASTE

            AGreaterNumericBinaryExpIR tooManyMatches = new AGreaterNumericBinaryExpIR();
            tooManyMatches.setType(new ABoolBasicTypeIR());
            tooManyMatches.setLeft(transAssist.getInfo().getExpAssistant().consIdVar(counterName, new ANatNumericBasicTypeIR()));
            tooManyMatches.setRight(transAssist.getInfo().getExpAssistant().consIntLiteral(1));

            String name = null;
            if(pattern instanceof AIdentifierPatternIR)
            {
                name = ((AIdentifierPatternIR) pattern).getName();
            }
            else
            {
                log.error("Expected pattern to be an identifier at this point. Got: " + pattern);
            }

            AAssignToExpStmIR resultAssign = new AAssignToExpStmIR();
            STypeIR elementType = transAssist.getElementType(setVar.getType());
            resultAssign.setTarget(transAssist.getInfo().getExpAssistant().consIdVar(resultVarName, elementType));
            resultAssign.setExp(transAssist.getInfo().getExpAssistant().consIdVar(name, elementType.clone()));

            AIfStmIR matchesCheck = new AIfStmIR();
            matchesCheck.setIfExp(tooManyMatches);
            matchesCheck.setThenStm(consIotaMultipleResultsError());
            matchesCheck.setElseStm(resultAssign);

            ABlockStmIR outerThen = new ABlockStmIR();
            outerThen.getStatements().add(incByOne);
            outerThen.getStatements().add(matchesCheck);

            AIfStmIR outerIf = new AIfStmIR();
            outerIf.setIfExp(predicate);
            outerIf.setThenStm(outerThen);

            return packStm(outerIf);
        }
        else
        {
            return null;
        }
    }

    @Override
    public SStmIR consIotaError(String msg) {

        AExternalExpIR assertError = new AExternalExpIR();
        assertError.setTargetLangExp(String.format("assert( false && \"%s\" )", msg));

        return CTransUtil.exp2Stm(assertError);
    }
}
