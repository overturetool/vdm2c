package org.overture.codegen.vdm2c.utils;

import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.APlusNumericBinaryExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.AIfStmIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.trans.quantifier.CounterData;
import org.overture.codegen.trans.quantifier.OrdinaryQuantifier;
import org.overture.codegen.trans.quantifier.QuantifierBaseStrategy;

import java.util.List;

public class CExists1QuantifierStrategy extends QuantifierBaseStrategy {

    protected CounterData counterData;

    public CExists1QuantifierStrategy(TransAssistantIR transformationAssistant, SExpIR predicate, String resultVarName, ILanguageIterator langIterator, ITempVarGen tempGen, IterationVarPrefixes iteVarPrefixes, CounterData counterData) {
        super(transformationAssistant, predicate, resultVarName, langIterator, tempGen, iteVarPrefixes);
        this.counterData = counterData;
    }

    @Override
    public List<AVarDeclIR> getOuterBlockDecls(AIdentifierVarExpIR setVar,
                                               List<SPatternIR> patterns) throws AnalysisException
    {
        if (firstBind)
        {
            AIdentifierPatternIR name = new AIdentifierPatternIR();
            name.setName(resultVarName);

            return packDecl(transAssist.getInfo().getDeclAssistant().consLocalVarDecl(counterData.getType().clone(), name, counterData.getExp().clone()));
        } else
        {
            return null;
        }
    }

    @Override
    public SExpIR getForLoopCond(AIdentifierVarExpIR setVar,
                                 List<SPatternIR> patterns, SPatternIR pattern)
            throws AnalysisException {

        SExpIR left = langIterator.getForLoopCond(setVar, patterns, pattern);
        SExpIR right = transAssist.consLessThanCheck(resultVarName, 2);

        SExpIR andExp = transAssist.consAndExp(left, right);

        return CTransUtil.newApply("toBool", andExp);
    }

    @Override
    public List<SStmIR> getForLoopStms(AIdentifierVarExpIR setVar,
                                       List<SPatternIR> patterns, SPatternIR pattern)
    {
        if(lastBind)
        {
            AIdentifierVarExpIR counterVar = transAssist.getInfo().getExpAssistant().consIdVar(resultVarName, counterData.getType());

            AAssignToExpStmIR incByOne = new AAssignToExpStmIR();
            incByOne.setTarget(counterVar);

            APlusNumericBinaryExpIR plusOne = new APlusNumericBinaryExpIR();
            plusOne.setType(new ABoolBasicTypeIR());
            plusOne.setLeft(counterVar.clone());
            plusOne.setRight(transAssist.getInfo().getExpAssistant().consIntLiteral(1));

            incByOne.setExp(plusOne);

            AIfStmIR condInc = new AIfStmIR();
            condInc.setIfExp(predicate.clone());
            condInc.setThenStm(incByOne);

            return packStm(condInc);
        }
        else
        {
            return null;
        }
    }
}
